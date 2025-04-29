package org.example.backend.domain.mail.controller;

import jakarta.mail.MessagingException;
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
    public String sendMail(
            @RequestParam("email") String email,
            @RequestParam("username") String username,
            @RequestParam("title") String title,
            @RequestParam("performStartAt") String performStartAt,
            @RequestParam("performEndAt") String performEndAt,
            @RequestParam("location") String location,
            @RequestParam("seatSection") String seatSection,
            @RequestParam("seatNum") String seatNum,
            @RequestParam("paymentAmount") int paymentAmount,
            @RequestParam("paymentDate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date paymentDate
    ) throws MessagingException {

        emailService.sendTicketMail(email, username, title,
                performStartAt, performEndAt, location,
                seatSection, seatNum, paymentAmount, paymentDate);

        return "티켓 예매 메일 발송 완료!";
    }

    //예매 취소 메일
    @PostMapping("/sendCancel")
    public String sendCancelMail(
            @RequestParam("email") String email,
            @RequestParam("username") String username,
            @RequestParam("title") String title,
            @RequestParam("performStartAt") String performStartAt,
            @RequestParam("performEndAt") String performEndAt,
            @RequestParam("location") String location,
            @RequestParam("seatSection") String seatSection,
            @RequestParam("seatNum") String seatNum,
            @RequestParam("paymentAmount") int paymentAmount,
            @RequestParam("paymentDate") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm") Date paymentDate
    ) throws MessagingException{
        emailService.sendCancelTicketMail(email, username, title,
                performStartAt, performEndAt, location,
                seatSection, seatNum, paymentAmount, paymentDate);

        return "티켓 예매 취소 메일 발송 완료!";
    }
}
