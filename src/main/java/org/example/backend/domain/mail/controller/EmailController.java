package org.example.backend.domain.mail.controller;

import org.example.backend.domain.mail.service.EmailService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/email")
public class EmailController {
	
    private final EmailService emailService;
    
    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }
    
    @PostMapping("/send")
    public String sendMail(
    	    @RequestParam("to") String to,
    	    @RequestParam("subject") String subject,
    	    @RequestParam("text") String text
    ) {
        emailService.sendSimpleMail(to, subject, text);
        return "메일 발송 완료!";
    }

}
