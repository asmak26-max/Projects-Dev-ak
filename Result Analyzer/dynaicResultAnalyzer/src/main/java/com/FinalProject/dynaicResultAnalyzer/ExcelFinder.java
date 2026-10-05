package com.FinalProject.dynaicResultAnalyzer;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;


public class ExcelFinder {
	 public static  Vector<Integer> findCellByContent(String filePath, String [] searchContent) {
		 Vector<Integer> indexes = new Vector<Integer>();
	        try (FileInputStream fis = new FileInputStream(filePath); Workbook workbook = new XSSFWorkbook(fis)) {
	            Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet

	            for (int i = 0; i <= sheet.getLastRowNum(); i++) { // Loop through all rows
	                Row row = sheet.getRow(i);
	                if (row == null) continue;

	                for (int j = 0; j < row.getLastCellNum(); j++) { // Loop through all columns
	                    Cell cell = row.getCell(j);
	                    String value = "";
	                    if (cell != null) {
	                        switch (cell.getCellType()) {
	                            case STRING:
	                                value = cell.getStringCellValue();
	                                break;
	                            case NUMERIC:
	                                value = String.valueOf(cell.getNumericCellValue());
	                                break;
	                            case BOOLEAN:
	                                value = String.valueOf(cell.getBooleanCellValue());
	                                break;
	                            case FORMULA:
	                                value = cell.getCellFormula();
	                                break;
	                            default:
	                                value = "";
	                        }
	                    }
	                    
	                    	for(int m = 0; m<searchContent.length; m++) {
	                    		if (searchContent[m].equalsIgnoreCase(value)) {
	                    			indexes.add(j);
	                    		}
	                    	}
	                    
	                }
	            }
	            
	        } catch (IOException e) {
	            System.out.println(e);
	        }
	        return indexes;
	    }
	 

	 public static int findSubColumns(String filePath, int mainColumnIndex) {
		 int subColumnCount = 0;
	        try (FileInputStream fis = new FileInputStream(filePath); Workbook workbook = new XSSFWorkbook(fis)) {
	            Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet

	            // Loop through all merged regions
	            for (int i = 0; i < sheet.getNumMergedRegions(); i++) {
	                CellRangeAddress region = sheet.getMergedRegion(i);
	                
	                // Check if the merged region starts in the target column
//	                System.out.println("On line 86 - "+mainColumnIndex);
	                if (region.getFirstColumn() == mainColumnIndex) {
	                    int span = region.getLastColumn() - region.getFirstColumn() + 1;
	                    subColumnCount += span;
	                    break;
	                }
	            }

	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        return subColumnCount;
	    }

	 public static Vector<Integer> findCellByContentInColumn(String filePath, String searchContent, int startingIndex) {
		 Vector<Integer> totalFailedStudents = new Vector<Integer>();
		  
	        try (FileInputStream fis = new FileInputStream(filePath); Workbook workbook = new XSSFWorkbook(fis)) {
	            Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet
	            for (int i = startingIndex; i <= sheet.getLastRowNum(); i++) { // Loop through all rows
	                Row row = sheet.getRow(i);
	                if (row == null) continue;
	                
	                int tempArr[] =  new int[row.getLastCellNum()];
	                
	                for(int j = 0; j < row.getLastCellNum(); j++) {
	                    Cell cell = row.getCell(j);
	                    String value = "";
	                    if (cell != null) {
	                        switch (cell.getCellType()) {
	                            case STRING:
	                                value = String.valueOf(cell.getStringCellValue());
	                                break;
	                            case NUMERIC:
	                                value = String.valueOf(cell.getNumericCellValue());
	                                break;
	                            case BOOLEAN:
	                                value = "";
	                                break;
	                            case FORMULA:
	                                value = "";
	                                break;
	                            default:
	                                value = "";
	                        }
	                    }
	                    
	                    for(int index=0; index<value.length(); index++) {
	                    	String Char  = value.charAt(index)+"";
		                		if (searchContent.equals(Char)) {
		                			tempArr[j] = 1;
		                	}else {
		                		tempArr[j] = 0;
		                	}
		                		
	                    }
	                    
	                    if(i == startingIndex) {
	                    	
	                    	totalFailedStudents.add(tempArr[j]);
	                    }
	                    else {
	                    	
	                    	Integer elem = tempArr[j] + totalFailedStudents.get(j);
	                    	
	                    	totalFailedStudents.insertElementAt(elem, j+1); 
	                    	totalFailedStudents.remove(j);
	                    }
	                }
	                
	       
	            }
	                

	        } catch (IOException e) {
	            e.printStackTrace();
	        }
	        return totalFailedStudents;
	    }

    
    public Map<String,Integer> analyze(String filePath, String[] searchContent) {
//        String filePath = "A:/SEM 4/TEST FILE.xlsx";

        // Call to find cell by content
//        String []searchContent = {"OOP","DSU","CGR","DMS","DTE"};  // Change this to search for any content
 
        Vector<Integer> subjectIndex = findCellByContent(filePath, searchContent); 
        Map<String,Integer> subject = new HashMap<>();
        
//        System.out.println(subjectIndex);
       if(!subjectIndex.isEmpty()) {
//    	   
           //Here we are mapping subject name with index in file of that subject
           
           
    	   if(subjectIndex.size() == searchContent.length) {
           for(int i=0; i<searchContent.length; i++) {
        		   subject.put(searchContent[i], subjectIndex.get(i));
        	   }
         //counting the no. of students failed per subject
           Vector<Integer> countOfPerSubject = new Vector<Integer>();

           //Here we have 'failed' students as per subject (Both Theory and Practical)
           Vector<Integer> finalArr = findCellByContentInColumn(filePath,"*",5);
           
           //This will loop for till every subject
           for(int i=0; i<finalArr.size(); i++) {

           	//Loop for every subject 
           	for(int j=0; j<searchContent.length; j++) {
           		
           		if(i == subject.get(searchContent[j])) {
           			countOfPerSubject.add(finalArr.get(i));
           		}
           	}
           	
           }
           
           //Updating this mapping for students that are failed as per subjects (Only Theory)
           //In future if we also want to calculate students failed in practical then make sure you just properly 
           //check 'finalArr' and map them with subjects (No need for extra work !!)
           for(int i=0; i<countOfPerSubject.size(); i++) {
           	subject.put(searchContent[i], countOfPerSubject.get(i));
           }
           
           System.out.println(subject);
           return subject;
           
           }else {
        	   subject.put("NoSuchSubject", 0);
        	   return subject;
           }
          
       }else {
    	   
    	   subject.put("NoSuchSubject", 0);
    	   return subject;
       }
    }
    
    public static Vector<String> findDataOfColumn(int column, String filePath) {
    	Vector<String> Data = new Vector<String>();
    	try (FileInputStream fis = new FileInputStream(filePath); Workbook workbook = new XSSFWorkbook(fis)) {
            Sheet sheet = workbook.getSheetAt(0); // Assuming data is in the first sheet
            for (int i = 0; i <= sheet.getLastRowNum(); i++) { // Loop through all rows
                Row row = sheet.getRow(i);
                if (row == null) continue;
                
                
                    Cell cell = row.getCell(column);
                    String value = "";
                    if (cell != null) {
                        switch (cell.getCellType()) {
                            case STRING:
                                value = String.valueOf(cell.getStringCellValue());
                                break;
                            case NUMERIC:
                                value = String.valueOf(cell.getNumericCellValue());
                                break;
                            case BOOLEAN:
                                value = "";
                                break;
                            case FORMULA:
                                value = "";
                                break;
                            default:
                                value = "";
                        }
                    }
                    Data.add(value);
            }
                
        } catch (IOException e) {
            e.printStackTrace();
        }
    	
        return Data;
    }
    
    public Map<String,Integer> analyzeFormatA(String filePath, String[] criteriaName) {
//    	String filePath = "A:/SEM 4/TEST FILE.xlsx";
//    	String criteriaName[] = {
//    			"firstClassDist","firstClass","secondClass",
//    			"thirdClass","fail","atkt","firstclassCon"};
    	
    	Integer[] criteriaCount = new Integer[criteriaName.length];
    	for(int i=0; i<criteriaName.length; i++) {
    		criteriaCount[i] = 0;
    	}
    	
    	String []searchContent = {"result"}; 
    	 
        Vector<Integer> subjectIndex = findCellByContent(filePath, searchContent); 
    	
        Vector<String> Data = findDataOfColumn(subjectIndex.getFirst(),filePath);
        
        
          for(int i=0; i<Data.size(); i++) {
        	  
        	  String unFormattedWord = Data.get(i).replaceAll("[ .]","");
        	  
        	  for(int j=0; j<criteriaName.length; j++) {
        		  String unFormattedCriteriaName = criteriaName[j].replaceAll("[ .]","");
        		  
        		  if(unFormattedWord.equalsIgnoreCase(unFormattedCriteriaName)) {
        			  criteriaCount[j] += 1;
        		  }
        	  }
          }
        
          Map<String,Integer> criteriaWiseStudents = new HashMap<>();
          for(int i=0; i<criteriaCount.length; i++) {
        	  criteriaWiseStudents.put(criteriaName[i], criteriaCount[i]);
          }
          
          return criteriaWiseStudents;
    }
    
    
  }

















