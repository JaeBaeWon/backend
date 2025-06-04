package org.example.backend.domain.mail.controller;

import jakarta.mail.MessagingException;
import org.example.backend.domain.mail.dto.EmailDto;
import org.springframework.web.bind.annotation.*;
import org.example.backend.domain.mail.service.EmailService;   // EmailService import
import org.springframework.format.annotation.DateTimeFormat;   // DateTimeFormat import
import java.util.Date;                                         // Date import

//이메일 전송을 담당하는 controller
@RestController
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    //티켓 예매 메일
    @PostMapping("/send")
    public String sendMail(@RequestBody EmailDto dto) throws MessagingException {
        emailService.sendTicketMail(
                dto.getEmail(),
                dto.getUsername(),
                dto.getTitle(),
                dto.getPerformStartAt(),
                dto.getPerformEndAt(),
                dto.getLocation(),
                dto.getSeatSection(),
                dto.getSeatNum(),
                dto.getPaymentAmount(),
                dto.getPaymentDate()
        );
        return "티켓 예매 메일 발송 완료!";
    }

    //예매 취소 메일
    @PostMapping("/sendCancel")
    public String sendCancelMail(@RequestBody EmailDto dto) throws MessagingException {
        emailService.sendCancelTicketMail(
                dto.getEmail(),
                dto.getUsername(),
                dto.getTitle(),
                dto.getPerformStartAt(),
                dto.getPerformEndAt(),
                dto.getLocation(),
                dto.getSeatSection(),
                dto.getSeatNum(),
                dto.getPaymentAmount(),
                dto.getPaymentDate()
        );
        return "티켓 예매 취소 메일 발송 완료!";
    }
}
