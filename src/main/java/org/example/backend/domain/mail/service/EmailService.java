package org.example.backend.domain.mail.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@Service
public class EmailService {

	@Autowired
	private JavaMailSender mailSender;

	public void sendSimpleMail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("kcklkb@gmail.com");  // 보낸 사람
        message.setTo(to);                       // 받는 사람
        message.setSubject(subject);             // 제목
        message.setText(text);                   // 내용
        mailSender.send(message);
	}
}
