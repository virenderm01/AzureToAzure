package com.automation;


import javax.mail.PasswordAuthentication;

public class SMTPAuthenticator extends javax.mail.Authenticator {
        	@Override
			public PasswordAuthentication getPasswordAuthentication() {
        	   String username = "apikey";
        	   String password = "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";
        	   return new PasswordAuthentication(username, password);
        	}
}
        	