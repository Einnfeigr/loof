package com.einnfeigr.taskApp.misc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class MailUtils {

	Logger log = LoggerFactory.getLogger(MailUtils.class);
	
	@Autowired
	private JavaMailSender sender;
	
	public void sendMail(String to, String subj, String text) {
        SimpleMailMessage message = new SimpleMailMessage(); 
        message.setFrom("noreply@loof.com");
        message.setTo(to); 
        message.setSubject(subj); 
        message.setText(text);
        sender.send(message);
	}
	
}
