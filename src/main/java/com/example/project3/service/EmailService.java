package com.example.project3.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
	@Autowired
    private JavaMailSender javaMailSender;
	
	 public void sendEmail(String jwt, String email) {
	        SimpleMailMessage message = new SimpleMailMessage();
	        message.setTo(email);
	        message.setSubject("비밀번호 재설정 링크입니다.");
	        message.setText("http://footballmoafront.azurewebsites.net/change?token="+jwt);
	        javaMailSender.send(message);
	    }

}
