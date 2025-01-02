package com.automation;



import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Attachments;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import com.sendgrid.helpers.mail.objects.Personalization;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;

       

public class MyEmailer {
          
          
           public void SendMail(String filenname,String from1, String companyname,String messagefromsender) throws Exception
           {
        	   
        		  Personalization personalization = new Personalization();
        	    Email from = new Email("admin@showmeinc.onmicrosoft.com");
        	   // String subject = "Sending with SendGrid is Fun";
        	    Email to = new Email();
        	   to.setEmail(from1);
        	   personalization.addTo(to);
        	  // Email to= new Email();
        	 // to.setEmail("sridhar.chigurupati@techdata.com");
        	  // personalization.addTo(to);
        	    //Email to= new Email();
         	 //  to.setEmail("kyle.rhynerson@techdata.com");
         	 //  personalization.addTo(to);
        	   
        	   
        	   
        	    Content content = new Content();
        	    content.setType("text/html");
        	    content.setValue(messagefromsender + " - This Email was sent by " + from + " <br/>");
        	    
        	
        	    //Mail mail = new Mail(from, subject, to, content);
        	    Mail mail = new Mail();
        	    mail.setFrom(from);
        	    mail.setSubject(companyname + " "+ "A2A Analysis");
        	    mail.addPersonalization(personalization);
        	    mail.addContent(content);
        	    
        	    Attachments attachment = new Attachments();
        	    String filepath = filenname;
        	    byte[] fileData = null;
        	    try {
        	        fileData = IOUtils.toByteArray(new FileInputStream(filepath));

        	    } catch (IOException ex) {
        	    }
        	    Base64 x = new Base64();
        	    String imageDataString = x.encodeAsString(fileData);
        	    
        	    
        	    attachment.setContent(imageDataString);
        	    attachment.setType("application/zip");
        	    attachment.setFilename(companyname+".zip");
        	    attachment.setDisposition("attachment");
        	    attachment.setContentId("A2A");
        	  
        	    mail.addAttachments(attachment);
        	    
        	    
        	    SendGrid sg = new SendGrid("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");
        	    //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
        	    //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
        	    Request request = new Request();
        	    try {
        	      request.setMethod(Method.POST);
        	      request.setEndpoint("mail/send");
        	      request.setBody(mail.build());
        	      Response response = sg.api(request);
        	      System.out.println(response.getStatusCode());
        	      System.out.println(response.getBody());
        	      System.out.println(response.getHeaders());
        	    } catch (IOException ex) {
        	      throw ex;
        	    }
        }

	public void SendMail(ByteArrayOutputStream zipped, String from1, String companyname, String messagefromsender) throws Exception
	{

		Personalization personalization = new Personalization();
		Email from = new Email("admin@showmeinc.onmicrosoft.com");
		// String subject = "Sending with SendGrid is Fun";
		Email to = new Email();
		to.setEmail(from1);
		personalization.addTo(to);
		// Email to= new Email();
		// to.setEmail("sridhar.chigurupati@techdata.com");
		// personalization.addTo(to);
		//Email to= new Email();
		//  to.setEmail("kyle.rhynerson@techdata.com");
		//  personalization.addTo(to);



		Content content = new Content();
		content.setType("text/html");
		content.setValue(messagefromsender + " - This Email was sent by " + from + " <br/>");


		//Mail mail = new Mail(from, subject, to, content);
		Mail mail = new Mail();
		mail.setFrom(from);
		mail.setSubject(companyname + " "+ "A2A Analysis");
		mail.addPersonalization(personalization);
		mail.addContent(content);

		Attachments attachment = new Attachments();

		Base64 x = new Base64();
		String imageDataString = x.encodeAsString(zipped.toByteArray());


		attachment.setContent(imageDataString);
		attachment.setType("application/zip");
		attachment.setFilename(companyname+".zip");
		attachment.setDisposition("attachment");
		attachment.setContentId("A2A");

		mail.addAttachments(attachment);


		SendGrid sg = new SendGrid("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");
		//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
		//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
		Request request = new Request();
		try {
			request.setMethod(Method.POST);
			request.setEndpoint("mail/send");
			request.setBody(mail.build());
			Response response = sg.api(request);
			System.out.println(response.getStatusCode());
			System.out.println(response.getBody());
			System.out.println(response.getHeaders());
		} catch (IOException ex) {
			throw ex;
		}
	}
        }
        