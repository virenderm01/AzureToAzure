package com.servletfiles;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.automation.AzuretoAzure;




@WebServlet(description = "Upload File To The Server", urlPatterns = { "/fileUploadServlet" })
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 10, maxFileSize = 1024 * 1024 * 30, maxRequestSize = 1024 * 1024 * 50)
public class FileUploadServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	public static final String UPLOAD_DIR = "uploadedFiles";

	/***** This Method Is Called By The Servlet Container To Process A 'POST' Request *****/
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		handleRequest(request, response);
	}

	public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		/***** Get The Absolute Path Of The Web Application *****/
		String applicationPath = getServletContext().getRealPath(""),
				uploadPath = applicationPath + UPLOAD_DIR+"",downloadPath = applicationPath +  "powershellscript";
		// String from = request.getParameter("from");
		//String recipient = request.getParameter("recipient");
		
      //  String companyname = request.getParameter("companyname");
      //  String message = request.getParameter("message");
        
        String customerName = request.getParameter("customerName").trim();
        String contactFullName = request.getParameter("contactFullName").trim();
        String emailAddress = request.getParameter("emailAddress").trim();
        String phoneNumber = request.getParameter("phoneNumber").trim();
        String billStreet = request.getParameter("addrLine1").trim();
        String billStreet1 = request.getParameter("addrLine2").trim();
        String billCity = request.getParameter("billCity").trim();
        String billState = request.getParameter("billState").trim();
        String billCountry = request.getParameter("billCountry").trim();
        String zipCode = request.getParameter("zipCode").trim();
        String tdSalesRepName = request.getParameter("tdSalesRepName").trim();
        String endCustomer = request.getParameter("endCustomer").trim();
        String mrr = request.getParameter("mrr").trim();
  
       String recipient ="kyle.rhynerson@techdata.com";
	   recipient = emailAddress;
       String message = "CustomerName:	"+ customerName +"<br/>"+ "ContactFullName:	"+contactFullName+"<br/>"+ "EmailAddress:	"+ emailAddress +"<br/>"+ "PhoneNumber:	"+ phoneNumber +"<br/>"+ "addrLine1:	"+ billStreet +"<br/>"+
    		  "addrLine2:	"+ billStreet1 +"<br/>"+
    		  "billCity:	"+ billCity +"<br/>"+
    		 "zipCode:	"+ zipCode +"<br/>"+
    		 "BillState:	"+ billState +"<br/>"+ "BillCountry:	"+  billCountry +"<br/>"+ "TDSalesRepName:"+  tdSalesRepName +"<br/>"+ "EndCustomer:"+  endCustomer +"<br/>"+ 
    		 "Monthly Recurring Revenue:	"+  mrr +"<br/>";
    		   
		File fileUploadDirectory = new File(uploadPath);
		if (!fileUploadDirectory.exists()) {
			fileUploadDirectory.mkdirs();
		}
		
		
		String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
				+ "0123456789";
				// create StringBuffer size of AlphaNumericString
				StringBuilder s = new StringBuilder(5);
				int y;
				for ( y = 0; y < 5; y++) {
				// generating a random number
				int index
				= (int)(AlphaNumericString.length()
				* Math.random());
				// add Character one by one in end of s
				s.append(AlphaNumericString
				.charAt(index));
				}
				String endcustomerx=s.toString();
				
		
		
		String fileName = "",fileuploadcompanydirectory="";
		customerName = customerName.replaceAll("[\\\\/:*?\"<>|]", "_");
		if(!endCustomer.isEmpty()) {
			customerName = customerName.replaceAll("[\\\\/:*?\"<>|]", "_");
			endCustomer = endCustomer.replaceAll("[\\\\/:*?\"<>|]", "_");
			fileuploadcompanydirectory=uploadPath+"\\"+customerName+"\\"+endCustomer;
		}
		else {
			endCustomer= endcustomerx;
			
			fileuploadcompanydirectory=uploadPath+"\\"+customerName + "\\"+endcustomerx;	
		}
		File fileUploadDirectorybycompany = new File(fileuploadcompanydirectory);
		if (!fileUploadDirectorybycompany.exists()) {
			fileUploadDirectorybycompany.mkdirs();
		}
	//	int total = UploadedFilesServlet.allFiles.length;
		UploadDetail details = null;


		List<UploadDetail> fileList = new ArrayList<UploadDetail>();
		AzuretoAzure azuretoAzure = new AzuretoAzure();
		ByteArrayOutputStream bos = null;
		for (Part part : request.getParts()) {
			System.out.println("name1:"  + part.getName() );
			if(part.getName().equalsIgnoreCase("fileUpload")) {
			fileName = extractFileName(part);
			System.out.println("name:"  + part.getName() );
			details = new UploadDetail();
			details.setFileName(fileName);
			if(fileName.endsWith("json"))
			{
				details.setFiletype("Json File.");
			}
			details.setFileSize(part.getSize() / 1024);
			try {
				Path tempFile = Files.createTempFile("uploaded_", "_" + fileName);
				part.write(tempFile.toString());
//				part.write(fileuploadcompanydirectory + File.separator + fileName);
				details.setUploadStatus("Success");
				
					if(fileName.endsWith("json")) {
					fileList.add(details);
					bos = azuretoAzure.createsow(tempFile, tempFile.getFileName().toString(),emailAddress,recipient,customerName,message,downloadPath,endCustomer);
					
					}
					else
					{
						
					}
			} catch (IOException ioObj) {
				details.setUploadStatus("Failure : "+ ioObj.getMessage());
			}
			
			}
			
			
		}
		azuretoAzure.sendzip(bos,endCustomer,emailAddress,customerName,message);
		request.setAttribute("uploadedFiles", fileList);
		RequestDispatcher dispatcher = request.getRequestDispatcher("/fileuploadResponse.jsp");
		dispatcher.forward(request, response);
	}

	/***** Helper Method #1 - This Method Is Used To Read The File Names *****/
	private String extractFileName(Part part) {
		String fileName = "", 
				contentDisposition = part.getHeader("content-disposition");
		String[] items = contentDisposition.split(";");
		for (String item : items) {
			if (item.trim().startsWith("filename")) {
				fileName = item.substring(item.indexOf("=") + 2, item.length() - 1);
				fileName = fileName.substring(fileName.lastIndexOf(File.separator) + 1);
			}
		}
		return fileName;
	}
}