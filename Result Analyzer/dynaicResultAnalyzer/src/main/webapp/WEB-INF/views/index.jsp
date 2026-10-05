<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
	<meta charset="UTF-8">
	<title>Result Analyzer</title>
	</head>
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
	<body>
	
	<div class="navbar">
        <a href="/" class="active">Extractor</a>
        <a href="/analyzer" >Analyzer</a>
    </div>
		<form method="post" name="myForm" id="myForm" enctype="multipart/form-data">
		Select Enrollment No./Seat No. <br>
		<select id="enrollOrSeat" name="selected">
			<option value="Seat No">Seat No</option>
			<option value="Enrollment No">Enrollment No</option>
		</select>
<!-- 	    <input type="text" name="enroll" id="enroll"> -->
	    <input type="text" name="seatNo" id="seatNo"><br>
	    	OR<br>
		 <label>Select Excel File:</label><br>
	     <input type="file" name="file" id="file" accept=".xls,.xlsx" ><br><br>
	
		Enter Website URL <br>
	    <input type="text" name="siteUrl" id="siteUrl"><br> <br>
	    
		For 'Whole Body' Section Enter - <br>
	     Enter Column No.<br> 
	    <input type="text" name="colNo" id="colNo"><br>
	     Enter Total Rows<br> 
	    <input type="text" name="rowNo" id="rowNo"><br> <br>
	
		For 'Head' Section -<br>
		Enter No. of Rows<br> 
	    <input type="text" name="headRowNo" id="headRowNo"><br> <br>
	    
	    For 'Total' Section -<br>
		Enter No. of Rows<br> 
	    <input type="text" name="totalRowNo" id="totalRowNo"><br> <br>
	    <br><input type="submit" value="Get Data"  id="btnGetData">
	    <input type="button" value="Quick Test" id="btnQuickTest">
	   </form>
	   
	   	<script src="DynamicFronthandHandler.js" defer></script>
	</body>
</html>