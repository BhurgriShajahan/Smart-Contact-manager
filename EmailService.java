package com.spring.service;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

	public boolean sendEmail(String subject , String message, String to) {
		
		boolean f = false;
		
//		String from = "techsoftindia2018@gmail.com";
		String from = "zulqarnainbhurgri58@gmail.com";
		String password = "salah72524";
		
		String host ="smtp.gmail.com";
		
		Properties properties = System.getProperties();
		
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "587");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");
		
		
		Session session = Session.getInstance(properties , new Authenticator() {

			@Override
			protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
				// TODO Auto-generated method stub
//				return new 	javax.mail.PasswordAuthentication("techsoftindia2018@gmail.com","");
//				return new 	javax.mail.PasswordAuthentication("zulqarnainbhurgri58@gmail.com","salah72524");
				return new 	javax.mail.PasswordAuthentication(from,password);
			}
			
//			@Override
//			protected PasswordAuthentication getPasswordAuthentication() {
//				return new 	PasswordAuthentication("techsoftindia2018@gmail.com","");
//			}
			
			
			
	});
		session.setDebug(true);
		
		MimeMessage m = new MimeMessage(session);
		
		try {
			m.setFrom(from);
//			m.setFrom(new InternetAddress(from));
			
			m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			
			m.setSubject(subject);
			
//			m.setText(message);
		    m.setContent(message,"text/html");
			
			javax.mail.Transport.send(m);
			
			System.out.println("sent success...............");
			
			f = true;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return f;
	}
	
}
