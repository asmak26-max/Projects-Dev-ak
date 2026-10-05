
//let enrollNo = document.getElementById("enroll");
let quickTestBtn = document.getElementById("btnQuickTest");
let getDataBtn = document.getElementById("btnGetData");
let file = document.getElementById("file");   
let form = document.getElementById("myForm");
let seatNo = document.getElementById("seatNo");

let siteUrl = document.getElementById("siteUrl");
let colNo = document.getElementById("colNo");
let rowNo = document.getElementById("rowNo");
let headRowNo = document.getElementById("headRowNo");
let totalRowNo = document.getElementById("totalRowNo");

let selection = document.getElementById("enrollOrSeat");
	quickTestBtn.disabled = true;
	getDataBtn.disabled = true;


	
	document.addEventListener("DOMContentLoaded", function () {
			
	seatNo.style.display = 'block';
//	enrollNo.style.display = 'none';
	selection.addEventListener('change', function(){
			
			if(selection.value == "Seat No"){
//				seatNo.style.display = 'block';
//				enrollNo.style.display = 'none';
			}else{
				alert("Selecting Enrollment No. might provide you result of previous semesters,which might cause you problem!!")
//				seatNo.style.display = 'none';
//				enrollNo.style.display = 'block';
			}
		})
		
			
	form.addEventListener('submit', function (event) {
	        event.preventDefault();

			if(checkAllFields() == true){
				var formData = new FormData(form);
					        var url = file.files.length > 0 ? "/automateBulkData" : "/automate";

					        fetch(url, {
					            method: "POST",
					            body: formData
					        })
					        .then(response => response.blob()) // Expecting file download as response
					        .then(blob => {
					            // If response is a file, trigger download
					            var a = document.createElement("a");
								
								if(blob.size != null){
						            a.href = window.URL.createObjectURL(blob);
						            a.download = "StudentResults.xlsx";
						            document.body.appendChild(a);
						            a.click();
						            document.body.removeChild(a);
								}else{
									alert("File is Empty");
								}
					        })
					        .catch(error => console.error("Error:", error));
			}else{
				alert("Please Fill All Fileds");
			}
	        
	    });
	});

function checkAllFields(){
	if(siteUrl.value != "" &&
		colNo.value != "" &&
		rowNo.value != "" &&
		headRowNo.value != "" &&
		totalRowNo.value !=""
	){
		return true;
	}else{
		return false;
	}
}

seatNo.addEventListener('change',disableFileSelection);
function disableFileSelection(){
	if(seatNo.value >0){
		file.disabled = true;	
		getDataBtn.disabled = false;		
	}else{
		file.disabled = false;		
		getDataBtn.disabled = true;				
	}
}
	
file.addEventListener('change',enableQuickTest);
function enableQuickTest(){
	console.log("in function");
	  if(file.files.length != 0){
//			enrollNo.disabled = true;
			seatNo.disabled = true;
			quickTestBtn.disabled = false;
			getDataBtn.disabled = false;
		  }else{
//			enrollNo.disabled = false;
			seatNo.disabled = false;
			getDataBtn.disabled = true;
			quickTestBtn.disabled = true;
		  }
}
	
	quickTestBtn.addEventListener("click",quickTest);
	function quickTest(){

		var formData = new FormData(form);
		console.log(formData);
		 fetch("/quickTest", {
		    method: 'POST',
		    body: formData
		}).then(response => response.text())
		.then(data => {
		   alert("Received Data: " + data);
		   fetched = true;
		})
		.catch(error => console.error("Error:", error));
		console.log("sent request");
	}
	
	
	
	
	
	
	
	
	
	