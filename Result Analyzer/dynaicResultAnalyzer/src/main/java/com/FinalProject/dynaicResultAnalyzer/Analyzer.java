package com.FinalProject.dynaicResultAnalyzer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Vector;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.Select;
import io.github.bonigarcia.wdm.WebDriverManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

public class Analyzer {

	public Vector<Vector<String>> wholeData = new Vector<Vector<String>>();
	
	public Vector<String> fetchData(String selected ,String EnrollNo, String url, int rownos, int colno, int rowno, int headrowno) {
		
		Vector<String> data = new Vector<String>();
		
		WebDriverManager.chromedriver().setup();
		WebDriver driver = new ChromeDriver();

		try {
			
			String fileName = url;
			int ColNo = colno;
            int RowNo = rownos;
            int headRowNo = headrowno;
            int totalRowNo = rowno;
            String enrollNo = EnrollNo;
			
			String fName = driver.getCurrentUrl();
			driver.get(fileName);

			if(fName != "") {
				
	            List<WebElement> inputTags = driver.findElements(By.tagName("input"));

	            Select dropdown = new Select(driver.findElement(By.tagName("select")));  
	            dropdown.selectByVisibleText(selected);  
	            
//	            if you want to find which one is the enroll field 
//	            	use document.getElementByTagname("input")
	            inputTags.get(3).sendKeys(enrollNo);

	            Thread.sleep(15000); 
//	            Thread.sleep(2000); 
	       //These are no. of rows in 'Total section'
	            if(ColNo < 0) {
	            	//I am setting default value in case user enters negative number
	            	// and 6 is the column no. where all obtained marks will be displayed
	            	ColNo = 6;
	            }
	            
	            // Fetch all table rows 
	            List<WebElement> rows = driver.findElements(By.tagName("tr")); 
	            //headRowNo+1 so that it perfectly starts with marks row
	            //RowNo - totalRowNo-1 : suppose - 31-3=28-1 = 27 which will be the index the marks rows complete
	            for (int j = headRowNo+1; j < RowNo-totalRowNo-1; j++) {
	            	//Fetch Particular cell of particular row
	            	List<WebElement> cells = rows.get(j).findElements(By.tagName("td"));
		            for(int i=0 ;i < cells.size(); i++) {
		            	//Fetching column no. from user to parse data of that column
		            	if(i == ColNo-1) {
		            		data.add(cells.get(i).getText());
//		            		System.out.print(i+") "+cells.get(i).getText() + " \n");
		            	}
		            }
	            }
	            //finding where starts 'Total' section i.e percentage section 
	            int totalStartsAt = RowNo-totalRowNo; // here we have count till body ends
	  
	            //Extracting percentage and total a student got
	            for (int j = totalStartsAt ; j < RowNo; j++) {
	            	List<WebElement> cells = rows.get(j).findElements(By.tagName("td"));
		            for(int i=0 ;i < cells.size(); i++) {
		            	//Fetching column no. from user to parse data of that column
		            	//Especially total and percentage
		            	if(i==1 || i==2 ) {
		            		data.add(cells.get(i).getText());
//		            		System.out.print(i+") "+cells.get(i).getText() + " \n");
		            	}
		            }
	            }
	            
			}else {
				data.add("ConnectionError");
				 driver.quit();
				return data;
			}
			
						
            } catch(Exception e) {
            	data.add("ConnectionError");
				driver.quit();
				return data;
    		} finally {
                driver.quit();
            }
		return data;
	}
	
//	public Vector<String> readexcel(HttpServletRequest request, HttpServletResponse res) throws IOException, ServletException{
//	
//		Vector<String> data = new Vector<String>();
//		
//		Part filePart = request.getPart("file");
//		if (filePart != null) {
//		    // Obtain the input stream of the uploaded file
//		    try (InputStream fileContent = filePart.getInputStream();
//		         Workbook workbook = WorkbookFactory.create(fileContent)) {
//
//		        // Get the first sheet
//		        Sheet sheet = workbook.getSheetAt(0);
//		        
//		        // Define the column index to extract (e.g., first column = 0)
//		        int columnIndex = 0;
//		        
//		        // Iterate through each row and extract only the specific column
//		        for (Row row : sheet) {
//		            Cell cell = row.getCell(columnIndex); // Get the specific column cell
//		            if (cell != null) {
//		                String cellValue = "";
//		                switch (cell.getCellType()) {
//		                    case NUMERIC:
//		                        if (DateUtil.isCellDateFormatted(cell)) {
//		                            cellValue = cell.getDateCellValue().toString();
//		                        } else {
//		                            cellValue = String.valueOf((long) cell.getNumericCellValue());
//		                        }
//		                        break;
//		                    default:
//		                        cellValue = "UNKNOWN";
//		                }
//		                // Add extracted column value to the list
//		                if(cellValue != "UNKNOWN") {
//		                	data.add(cellValue);		                	
//		                }
//		            }
//		        }
//		    } catch (Exception e) {
//		        e.printStackTrace();
//		    }
//		}
//		return data;
//		
//	}

	
	public Vector<String> readExcelFromSheet(Sheet sheet) {
	    Vector<String> data = new Vector<>();
	    int columnIndex = 0; // Extract data from column 1

	    for (Row row : sheet) {
            Cell cell = row.getCell(columnIndex); // Get the specific column cell
            if (cell != null) {
                String cellValue = "";
                switch (cell.getCellType()) {
                    case NUMERIC:
                        if (DateUtil.isCellDateFormatted(cell)) {
                            cellValue = cell.getDateCellValue().toString();
                        } else {
                            cellValue = String.valueOf((long) cell.getNumericCellValue());
                        }
                        break;
                    default:
                        cellValue = "UNKNOWN";
                }
                // Add extracted column value to the list
                if(cellValue != "UNKNOWN") {
                	data.add(cellValue);		                	
                }
            }
        }
	    return data;
	}
	
	public void writeDataIntoExistingExcel(Sheet sheet, Vector<Vector<String>> wholeData) {
	    for (int rowIndex = 0; rowIndex < wholeData.size(); rowIndex++) {
	        Row row = sheet.getRow(rowIndex);
	        if (row == null) { // Ensure row exists
	            row = sheet.createRow(rowIndex);
	        }

	        int startCol = row.getLastCellNum(); // Get next empty column

	        if (startCol == -1) { // If row is empty, start from column 1
	            startCol = 1;
	        }

	        Vector<String> rowData = wholeData.get(rowIndex);
	        for (int colIndex = 0; colIndex < rowData.size(); colIndex++) {
	            Cell cell = row.createCell(startCol + colIndex);
//	            cell.setCellValue(rowData.get(colIndex));
	            
	            String cellValue = rowData.get(colIndex);

	            if (cellValue.matches("-?\\d+(\\.\\d+)?")) { // Regex for integer or decimal
                    cell.setCellValue(Double.parseDouble(cellValue));
                } else {
                    cell.setCellValue(cellValue);
                }
	            
	        }
	    }
	}

	public ByteArrayInputStream writeIntoExcel(Vector<Vector<String>> data) throws IOException {
		Workbook workbook = new XSSFWorkbook();
		ByteArrayOutputStream out  = new ByteArrayOutputStream();
		
		try {			
			Sheet sheet = (Sheet) workbook.createSheet("StudentData");
			
			for(int j=0; j<data.size(); j++) {
			
				Row row = sheet.createRow(j);
				
				for(int i=0; i<data.get(j).size(); i++) {
					
					Cell cell = row.createCell(i);
					cell.setCellValue(data.get(j).get(i));
				}
				
			}
			workbook.write(out);
			return new ByteArrayInputStream(out.toByteArray());
			
		}catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		finally {
			workbook.close();
			out.close();
		}
		
	}
		
	public ByteArrayInputStream writeIntoExcelFormatB(Vector<Vector<String>> data) throws IOException {
	    Workbook workbook = new XSSFWorkbook();
	    ByteArrayOutputStream out = new ByteArrayOutputStream();

	    try {
	        Sheet sheet = workbook.createSheet("FORMAT B");

	        // Create a bold style for the headers
	        CellStyle headerStyle = workbook.createCellStyle();
	        Font headerFont = workbook.createFont();
	        headerFont.setBold(true);
	        headerStyle.setFont(headerFont);
	        headerStyle.setAlignment(HorizontalAlignment.CENTER);
	        headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
	        headerStyle.setBorderTop(BorderStyle.THIN);
	        headerStyle.setBorderBottom(BorderStyle.THIN);
	        headerStyle.setBorderLeft(BorderStyle.THIN);
	        headerStyle.setBorderRight(BorderStyle.THIN);

	        // Create a normal style with borders for data rows
	        CellStyle dataStyle = workbook.createCellStyle();
	        dataStyle.setBorderTop(BorderStyle.THIN);
	        dataStyle.setBorderBottom(BorderStyle.THIN);
	        dataStyle.setBorderLeft(BorderStyle.THIN);
	        dataStyle.setBorderRight(BorderStyle.THIN);
	        dataStyle.setAlignment(HorizontalAlignment.CENTER);

	        // Header Row
	        Row headerRow = sheet.createRow(0);
	        String[] headers = {
	            "Sr. No.", "Name of Subject", "Actual no. of students appeared",
	            "Total no. of students passed", "1st class with Distinction",
	            "1st class", "2nd class", "Pass class", "% of Passing", "Name of Teacher"
	        };

	        for (int i = 0; i < headers.length; i++) {
	            Cell cell = headerRow.createCell(i);
	            cell.setCellValue(headers[i]);
	            cell.setCellStyle(headerStyle);
	        }

	        // Writing Data with Correct Type Handling
	        for (int j = 0; j < data.size(); j++) {
	            Row row = sheet.createRow(j + 1); // Data starts from row index 1

	            for (int i = 0; i < data.get(j).size(); i++) {
	                Cell cell = row.createCell(i);
	                String cellValue = data.get(j).get(i);

	                // Check if the value is a number
	                if (cellValue.matches("-?\\d+(\\.\\d+)?")) { // Regex for integer or decimal
	                    cell.setCellValue(Double.parseDouble(cellValue));
	                } else {
	                    cell.setCellValue(cellValue);
	                }

	                cell.setCellStyle(dataStyle);
	            }
	        }

	        // Auto-size columns for better visibility
	        for (int i = 0; i < headers.length; i++) {
	            sheet.autoSizeColumn(i);
	        }

	        workbook.write(out);
	        return new ByteArrayInputStream(out.toByteArray());

	    } catch (Exception e) {
	        e.printStackTrace();
	        return null;
	    } finally {
	        workbook.close();
	        out.close();
	    }
	}
	
	public ByteArrayInputStream writeIntoExcelFormatA(Vector<Vector<String>> data, Vector<String> headers) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            Sheet sheet = workbook.createSheet("FORMAT B");

            // Create styles
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setWrapText(true);
            headerStyle.setBorderRight(BorderStyle.THIN);

            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            dataStyle.setAlignment(HorizontalAlignment.CENTER);

            // Header Row
            Row headerRow = sheet.createRow(0);
//            String[] headers = {
//                "Class/ Year", "No. of Students registered for Exam", "No. of students actually appeared", 
//                "1st class with Distinction", "1st class", "2nd class", "Pass class", 
//                "Pass Without ATKT", "With ATKT", "Total student passed", "Total No. of student Failed", 
//                "Total passing % without ATKT", "Total passing % with ATKT"
//            };
            headers.add("Pass Without ATKT");
            headers.add("With ATKT");
            headers.add("No. of Students registered for Exam");
           

            for (int i = 0; i < headers.size(); i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i).toUpperCase());
                cell.setCellStyle(headerStyle);
            }

            // Writing Data
            for (int j = 0; j < data.size(); j++) {
                Row row = sheet.createRow(j + 1); // Data starts from row index 1
                for (int i = 0; i < data.get(j).size(); i++) {
                    Cell cell = row.createCell(i);
                    String cellValue = data.get(j).get(i);
                    
                    if (cellValue.matches("-?\\d+(\\.\\d+)?")) { // If it's a number
                        cell.setCellValue(Double.parseDouble(cellValue));
                    } else {
                        cell.setCellValue(cellValue);
                    }
                    
                    cell.setCellStyle(dataStyle);
                }
            }

            // Auto-size columns
            for (int i = 0; i < headers.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            workbook.close();
            out.close();
        }
    }

}
