package com.FinalProject.dynaicResultAnalyzer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class HomeController {

	@GetMapping("/")
	public String home() {
		return "index";
	}
	
	@PostMapping("/automate")
	public ResponseEntity<Resource> automate(AutomationParameter obj ) throws IOException, ServletException {
		
		Vector<String> studData = new Vector<String>();
		Analyzer analyzer = new Analyzer();
		String selection = obj.getSelected();
		String enrollOrSeat;
		String Url = obj.getSiteUrl();
		int RowNo = obj.getRowNo();
		int ColNo = obj.getColNo();
		int totalRowNo = obj.getTotalRowNo();
		int headRowNo = obj.getHeadRowNo();
	

		enrollOrSeat = obj.getSeatNo();
			
			studData = analyzer.fetchData(selection, enrollOrSeat, Url, RowNo, ColNo, totalRowNo, headRowNo);
			analyzer.wholeData.add(studData);
			
			String fileName = "StudentResults.xlsx";

			ByteArrayInputStream actualData = analyzer.writeIntoExcel(analyzer.wholeData);;
			
			if (studData.firstElement().equals("ConnectionError")) {
				studData.remove("ConnectionError");
            }
            	InputStreamResource file = new InputStreamResource(actualData);
				ResponseEntity<Resource> body = ResponseEntity.ok()
						.header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename="+fileName)
						.contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
						.body(file);
				return body;
            	
            
			
	}
	

	@PostMapping("/automateBulkData")
	public ResponseEntity<Resource> automateBulkData(
	        @RequestParam("file") MultipartFile file,
	        @ModelAttribute AutomationParameter obj) throws IOException {

	    Analyzer analyzer = new Analyzer();

	    String url = obj.getSiteUrl();
	    int rowNo = obj.getRowNo();
	    int colNo = obj.getColNo();
	    String selection = obj.getSelected();
	    int totalRowNo = obj.getTotalRowNo();
	    int headRowNo = obj.getHeadRowNo();

	    try (InputStream fileContent = file.getInputStream();
	         Workbook workbook = WorkbookFactory.create(fileContent);
	         ByteArrayOutputStream out = new ByteArrayOutputStream()) {

	        // Get the first sheet of the uploaded Excel file
	        Sheet sheet =  workbook.getSheetAt(0);
	        Vector<String> xlData = analyzer.readExcelFromSheet(sheet);
	        

	        for (int i = 0; i < xlData.size(); i++) {
	            String enrollNo = xlData.get(i);
	            Vector<String> studData = analyzer.fetchData(selection,enrollNo, url, rowNo, colNo, totalRowNo, headRowNo);
	            

	            if (studData.firstElement().equals("ConnectionError")) {
	                i--;
	            }else {
	            	analyzer.wholeData.add(studData);	
	            }
	        }

	        // Write the fetched data back into the same workbook
	        analyzer.writeDataIntoExistingExcel(sheet, analyzer.wholeData);
	        
	        // Save the modified workbook into a ByteArrayOutputStream
	        workbook.write(out);


	        // Prepare file for download
	        InputStreamResource fileResource = new InputStreamResource(new ByteArrayInputStream(out.toByteArray()));
	        return ResponseEntity.ok()
	                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getOriginalFilename())
	                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
	                .body(fileResource);

	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	    }
	}

	
	@PostMapping("/quickTest")
	public  ResponseEntity<String> quickTest(@RequestParam("file") MultipartFile file,@ModelAttribute AutomationParameter obj ) throws IOException {
		
		 Analyzer analyzer = new Analyzer();

		    String url = obj.getSiteUrl();
		    int rowNo = obj.getRowNo();
		    int colNo = obj.getColNo();
		    String selection = obj.getSelected();
		    int totalRowNo = obj.getTotalRowNo();
		    int headRowNo = obj.getHeadRowNo();
		
		
		try (InputStream fileContent = file.getInputStream();
		         Workbook workbook = WorkbookFactory.create(fileContent);
		         ByteArrayOutputStream out = new ByteArrayOutputStream()) {

		        // Get the first sheet of the uploaded Excel file
		        Sheet sheet =  workbook.getSheetAt(0);
		        Vector<String> xlData = analyzer.readExcelFromSheet(sheet);

				String EnrollNo = xlData.get(0);
				
		         Vector<String> studData = analyzer.fetchData(selection,EnrollNo, url, rowNo, colNo, totalRowNo, headRowNo);
		            

				return ResponseEntity.ok(studData.toString());
		} catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
	    }
	}
	
	@RequestMapping("/analyzer")
	public  String changePage() {
		return "analyzer";
	}

	@PostMapping("/formatBAnalysation")
	public ResponseEntity<?> formatBAnalysation(@RequestParam("file") MultipartFile file,
			@RequestParam("noOfStudents") String noOfStudents,
			@RequestParam("subjectNames") String subjectNames) throws IllegalStateException, IOException {
		
		String uploadDir = System.getProperty("user.dir");
		ExcelFinder obj = new ExcelFinder();
		
		 Analyzer analyzer = new Analyzer();
		 
		subjectNames = subjectNames.replace(" ", "");
		String[] subjectArray = subjectNames.split(",");
	
		File dir = new File(uploadDir);
		 if (!dir.exists()) {
		        dir.mkdirs();
		    }
		 String filePath = uploadDir.replace('\\', '/') + "/" + file.getOriginalFilename();
		 File destinationFile = new File(filePath);
		 
		 file.transferTo(destinationFile);
		
		 Map<String,Integer> subject  = obj.analyze(filePath, subjectArray);
		 
		 if(subject.get("NoSuchSubject") != null) {
			 
			 return ResponseEntity.ok("No Such Subject Found !!");
		 }
		 else {
			 
			 Vector<Vector<String>> data = new Vector<>();
			 int srno = 1;
			 ExcelFinder finder = new ExcelFinder();
			 
			 for(int i=0; i<subject.size(); i++) {
				 int studentsPassed = subject.get(subjectArray[i]);
				 int noOfStuds = Integer.parseInt(noOfStudents);
				 
				 if(noOfStuds >= studentsPassed) {
					 studentsPassed =  noOfStuds - studentsPassed;
				 }
				 String criteriaNames[] = {
						 "FirstClassDist", "FirstClass", "SecondClass",
				            "ThirdClass"
				 };
				 
				 Map<String,Integer> criteriaWiseStudents = new HashMap<>();
				 criteriaWiseStudents = finder.analyzeFormatA(filePath, criteriaNames);
				
				 int FirstClassDist = criteriaWiseStudents.get("FirstClassDist");
				 int FirstClass = criteriaWiseStudents.get("FirstClass");
				 int SecondClass = criteriaWiseStudents.get("SecondClass");
				 int ThirdClass = criteriaWiseStudents.get("ThirdClass");
				 int totalStuds = FirstClassDist+FirstClass+SecondClass+ThirdClass;
				 if(totalStuds <= noOfStuds) {
					 double totalStudentPassed = ( (double) studentsPassed/noOfStuds)*100;
					 String formattedPercentage = String.format("%.2f%%", totalStudentPassed);
					 data.add(new Vector<>(Arrays.asList(""+srno,
							 subjectArray[i].toUpperCase(),
							 noOfStudents,
							 String.valueOf(studentsPassed),
							 String.valueOf(FirstClassDist),
							 String.valueOf(FirstClass),
							 String.valueOf(SecondClass),
							 "", formattedPercentage,
							 "")));
				 }else {
					 data.add(new Vector<>(Arrays.asList(""+srno,
							 subjectArray[i].toUpperCase(),
							 noOfStudents,
							 "The No of students you ","have entered are less ","than students there",
							 "are in xlsx file", " Please Retry","!!",
							 "")));
				 }
				 
				 srno++;
			 }
			 
			 ByteArrayInputStream actualData = analyzer.writeIntoExcelFormatB(data);
			 
			 InputStreamResource Analyzedfile = new InputStreamResource(actualData);
				ResponseEntity<Resource> body = ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION)
				.contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
				.body(Analyzedfile);
				
				
				return body;

		 }
	}
	
	@PostMapping("/formatAAnalalysation")
	public ResponseEntity<?> formatAAnalalysation(@RequestParam("file") MultipartFile file,
			@RequestParam("subjectNames") String resultType,
			@RequestParam("noOfStudents") String noOfStudents) throws IllegalStateException, IOException {
		
		String uploadDir = System.getProperty("user.dir");
		Analyzer analyzer = new Analyzer();
		
		File dir = new File(uploadDir);
		 if (!dir.exists()) {
		        dir.mkdirs();
		    }
		 String filePath = uploadDir.replace('\\', '/') + "/" + file.getOriginalFilename();
		 File destinationFile = new File(filePath);
		 
		 file.transferTo(destinationFile);
		
		ExcelFinder finder = new ExcelFinder();
		String criteriaNames[] = resultType.split(",");
		
		Map<String,Integer> criteriaWiseStudents = new HashMap<>();
		criteriaWiseStudents = finder.analyzeFormatA(filePath, criteriaNames);
		
		Vector<Vector<String>> data = new Vector<>();

        // Example row (same structure as the image)
        Vector<String> row1 = new Vector<>();
        Vector<String> header = new Vector<>();

        int passed = 0;
        int passedWithoutATKT = 0;
        for(int i=0; i<criteriaNames.length; i++) {      
        	header.add(criteriaNames[i]);
        	 String unFormattedCriteriaName = criteriaNames[i].replaceAll("[ .]","");
        	if(unFormattedCriteriaName.equalsIgnoreCase("fail")) {
        		
        	}else {
        		passed+= criteriaWiseStudents.get(criteriaNames[i]);
        		if(!unFormattedCriteriaName.equalsIgnoreCase("atkt")) {
        			passedWithoutATKT +=criteriaWiseStudents.get(criteriaNames[i]);
        		}
        	}
        	row1.add(String.valueOf(criteriaWiseStudents.get(criteriaNames[i])));    // 1st class with Distinction
        }
        
        row1.add(String.valueOf(passedWithoutATKT));   
        row1.add(String.valueOf(passed));   
        row1.add(noOfStudents);
        
        data.add(row1);
		ByteArrayInputStream actualData = analyzer.writeIntoExcelFormatA(data, header);
		
		InputStreamResource Analyzedfile = new InputStreamResource(actualData);
		ResponseEntity<Resource> body = ResponseEntity.ok()
		.header(HttpHeaders.CONTENT_DISPOSITION)
		.contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
		.body(Analyzedfile);
		
		
		return body;
	}
}
