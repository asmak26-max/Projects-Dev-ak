
let btnGetData = document.getElementById("btnGetData");
let form = document.getElementById("myFormmyFormAnalyzer");
let file = document.getElementById("file");
let selectedFormat = document.getElementById("selectedFormat");
let subjectNames = document.getElementById("subjectNames");
let subjectLabel = document.getElementById("subjectLabel");
let criteriaLabel = document.getElementById("criteriaLabel");
let changeCriteriaNameBtn = document.getElementById("changeCriteriaNameBtn");

showFormatAData()
selectedFormat.addEventListener("change", function () {

    if (selectedFormat.value === "formatA") {
        showFormatAData();

    } else {
		subjectLabel.innerHTML = "Please enter subject names in order same as they are in given (.xlsx) file<br>Enter Subject Names:";
		subjectLabel.style.display = "block";

		criteriaLabel.style.display = "none";
		changeCriteriaNameBtn.style.display = "none";
		
		subjectNames.style.width = "150px";
		subjectNames.style.height = "20px";
		subjectNames.disabled = false;
		subjectNames.value = "";
    }
});

function showFormatAData(){
	subjectLabel.innerText = "Please check these types of results";
	subjectLabel.style.display = "block";
	let defaultCriteria = ["FirstClassDist", "FirstClass", "SecondClass",
	            "ThirdClass", "Fail", "ATKT", "FirstclassCon"];
	        subjectNames.style.width = "400px";
			subjectNames.style.height = "40px";
	        subjectNames.value = defaultCriteria.join(", ");
			subjectNames.disabled = true;
			
			criteriaLabel.style.display = "block";
			changeCriteriaNameBtn.style.display = "block";
			
			subjectNames.addEventListener("focusout",function(){
				if(selectedFormat.value === "formatA"){
					subjectNames.disabled = true;
				}
			});
			
			changeCriteriaNameBtn.addEventListener("click", function(){
				subjectNames.disabled = false;
			});
			
}

form.addEventListener('submit', function (event) {
    event.preventDefault();

	if (selectedFormat.value === "formatA") {
		subjectNames.disabled = false;
		sendRequest("formatAAnalalysation");
		
	}else{
		subjectNames.disabled = false;
		sendRequest("formatBAnalysation");	
	}
    
});

function sendRequest(methodName){
	if (checkAllFields()) {
	        if (file.files.length > 0) {
	            var formData = new FormData(form);

	            fetch("/"+methodName, {
	                method: "POST",
	                body: formData
	            })
	            .then(response => {
	                // Check Content-Type to determine if response is a file or text
	                const contentType = response.headers.get("content-type");

	                if (contentType.includes("application/json") || contentType.includes("text/plain")) {
	                    return response.text(); // Expecting a text response
	                } else {
	                    return response.blob(); // Expecting a file response
	                }
	            })
	            .then(data => {
	                if (typeof data === "string") {
	                    // Handle text response (e.g., error message or success message)
	                    alert("Received Message: " + data);
	                } else if (data instanceof Blob) {
	                    // Handle file response
	                    if (data.size > 0) {
	                        var a = document.createElement("a");
	                        a.href = window.URL.createObjectURL(data);
	                        a.download = methodName+".xlsx";
	                        document.body.appendChild(a);
	                        a.click();
	                        document.body.removeChild(a);
	                    } else {
	                        alert("Response - File is Empty!!");
	                    }
	                }
	            })
	            .catch(error => {
	                console.error("Error:", error);
	                alert("An error occurred while processing your request.");
	            });
	        } else {
	            alert("File is Empty!!");
	        }
	    } else {
	        alert("Please Fill All Fields");
	    }
		
}

function checkAllFields(){
	if(subjectNames.value != "" 
	){
		return true;
	}else{
		return false;
	}
}


		