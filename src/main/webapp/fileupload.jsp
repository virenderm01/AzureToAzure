<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
	<head>
	    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	    <title>Azure to Azure Automation</title>
	    
	    <link rel="stylesheet" href="resource/css/main.css" />
	    <script type="text/javascript" src="resource/js/jquery-3.2.1.min.js"></script>
	    <script type="text/javascript" src="resource/js/fileupload.js"></script>
	</head>
	<body>
	


	<a id="downloadLink" class="hyperLink" href="<%=request.getContextPath()%>/downloadServlet"> Download PowerShell Script</a>
		    
		<div class="panel">	 
	        <h3>Instructions for running the discovery script</h3>
			<h4>1.	Save the text version of the script with a .ps1 extension to your local system.<br>
				2.	Ensure you have a c:\temp folder on your system.<br>
				3.	Open PowerShell, browse to the location of the saved script, and run AzureSubDiscovery.ps1.<br>
				4.	Enter a username that has at least read-only permissions to the subscription(s) you want to gather info, and click Next.<br>
					NOTE: If a subscription contains Classic Cloud Services, a second Login prompt will appear. The user account must have 'Co-Administrator' permissions (See the REQUIREMENTS section above for guidance).
 					<br>
				5.	Enter a password and click Sign-in.<br>
				6.	Click a subscription or CTRL-CLICK to select multiple subscriptions, and then click OK.<br>
				7.	In the file upload section below slect the output file in the C:\temp folder and click upload.</h4>			
	    </div>
		
		<div class="panel">
	        <h1>Customer Information</h1>
			<h4>Fields marked with * are required fields</h4>
	        <form id="fileUploadForm" method="post" action="fileUploadServlet" enctype="multipart/form-data">
	        <table border="0" width="100%" align="center">
            <tr>
                <td width="40%">Legal Company Name * </td>
                <td><input type="text" name="customerName" required oninvalid="this.setCustomValidity('Enter Company Name Here')" oninput="this.setCustomValidity('')" size="50"/></td>
            </tr>
            <tr>
				<td valign="middle" >Contact Information </td>
               
               	<td>
					<table>
						<tr> 
							<td width="50%">Name (First Last) * 
							<input type="text" name="contactFullName" required oninvalid="this.setCustomValidity('Enter Name Here')" oninput="this.setCustomValidity('')" size="50"/></td>
							</tr>
							<tr>
								 <td>Email Address * <input type="text" pattern="[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,4}$" id = "emailAddress" name="emailAddress" required oninvalid="this.setCustomValidity('Enter Valid email Here')" oninput="this.setCustomValidity('')" size="50"/></td>
								</tr>
							<tr>
								 <td>Phone Number * <input type="text" pattern="[0-9]+" name="phoneNumber" size="50" maxlength="10" minlength="10" required oninvalid="this.setCustomValidity('Enter enter a valid telephone number')" oninput="this.setCustomValidity('')" size="50"/></td>
							</tr>
							</table>
				</td>
            </tr>      
			<tr>                
				<td valign="middle" >Bill-to-Address </td>
               
               	<td>
					<table>
						<tr> 
							<td width="50%">Address Line 1 * 
								<input type="text" name="addrLine1" required oninvalid="this.setCustomValidity('This field cannot be empty')" oninput="this.setCustomValidity('')" size="50"/>
							</td>
						</tr>
						<tr> 
							<td width="50%">Address Line 2 * 
								<input type="text" name="addrLine2" size="50"/>
							</td>
						</tr>
						<tr> 
							<td width="50%">City * </br>
								<input type="text" pattern="^[A-Za-z -]+$" name="billCity" required oninvalid="this.setCustomValidity('Enter Valid City Name Here')" oninput="this.setCustomValidity('')" size="50"/>								
							</td>
						</tr>
						<tr>
							<td>State/Province * <input type="text" name="billState" required oninvalid="this.setCustomValidity('Enter Valid State/Province Name Here')" oninput="this.setCustomValidity('')" size="50"/></td>
						</tr>
							<tr>
								 <td>Country * </br> <input type="text" name="billCountry" required oninvalid="this.setCustomValidity('Enter Valid Country Name Here')" oninput="this.setCustomValidity('')" size="50"/></td>
							</tr>
						<tr> 
							<td width="50%">Zip/Postal Code * 
								<input type="text" name="zipCode" required oninvalid="this.setCustomValidity('Enter Valid Zip/Postal Code Here')" oninput="this.setCustomValidity('')" size="50"/>
							</td>
						</tr>
					</table>
				</td>
            </tr>          
			<tr>
                <td>Techdata Sales Rep Name * </td>
                <td><input type="text" name="tdSalesRepName" required oninvalid="this.setCustomValidity('This filed cannot be empty')" oninput="this.setCustomValidity('')" size="50"/></td>
            </tr>          
			<tr>
                <td>End Customer Name </td>
                <td><input type="text" name="endCustomer" size="50"/></td>
            </tr>          
			<tr>
                <td>Monthly Reoccuring Revenue </td>
                <td><input type="text" name="mrr" size="50" /></td>
            </tr>          

        
		
	    
            <tr>
                <td colspan="2" align="center">
<div class="form_group">
	                <label>Upload File</label><span id="colon">: </span><input id="fileAttachment" type="file" name="fileUpload" accept=".json" multiple="multiple" required />
	                <span id="fileUploadErr">Please Upload A File!</span>
	            </div>
				
	            </td>
	            
            </tr>
<tr>
<td colspan="2" align="center">
<button id="upload" type="submit" class="btn btn_primary" onclick = "validateFunc()">Submit</button>
 </tr>

        </table>

	        </form>
	    </div>
		
	    
	     
	    
	</body>
	<script>
		function validateFunc()
		{
			var mailformat = "/^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/";
			if!(document.getElementById("emailAddress").value.match(mailformat))
			{
				alert("Please enter a valid email address.");    //The pop up alert for an invalid email address
				return false;
			}
			
		}
		</script>
</html>