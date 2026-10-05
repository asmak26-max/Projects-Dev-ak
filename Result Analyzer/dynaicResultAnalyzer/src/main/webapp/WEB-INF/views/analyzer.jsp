<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Analyzer</title>
    <style>
        body {
            font-family: Arial, sans-serif;
        }
        .navbar {
            background-color: #333;
            overflow: hidden;
        }
        .navbar a {
            float: left;
            display: block;
            color: white;
            text-align: center;
            padding: 14px 16px;
            text-decoration: none;
        }
        .navbar a:hover {
            background-color: #ddd;
            color: black;
        }
        .active {
            background-color: #04AA6D;
        }
        .container {
            padding: 20px;
        }
    </style>
</head>
<body>

    <div class="navbar">
        <a href="/">Extractor</a>
        <a href="/analyzer" class="active">Analyzer</a>
    </div>


	<form method="post" name="myFormAnalyzer" id="myFormmyFormAnalyzer" enctype="multipart/form-data">

		 <label>Select Excel File:</label><br>
	     <input type="file" name="file" id="file" accept=".xls,.xlsx" ><br><br>
		
		<label>Select Format</label><br>
		<select id="selectedFormat" name="selectedFormat">
			<option value="formatA">FORMAT A</option>
			<option value="formatB">FORMAT B</option>
		</select>
		<br><br>
		<label id="subjectLabel">Please enter subject names in order same as they are in given (.xlsx) file<br>
		 Enter Subject Names:</label>
		<input type="text" name="subjectNames" id="subjectNames"><br>
		<label id="criteriaLabel">Do you want to change them?</label>
		<input type="button" id="changeCriteriaNameBtn" value="Change Type">
		
		<br><br>
		
		<label>Enter No. of Students appeared in Exam:</label><br>
		<input type="number" name="noOfStudents" id="noOfStudents"><br><br>
	    <br><input type="submit" value="Get Data"  id="btnGetData">
	  </form>
	  
	   <script src="DynamicAnalyzerFronthandHandler.js" defer></script>
</body>
</html>















