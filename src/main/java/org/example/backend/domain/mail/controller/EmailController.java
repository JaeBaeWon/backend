package org.example.backend.domain.mail.controller;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.example.backend.domain.mail.dto.EmailDto;
import org.springframework.web.bind.annotation.*;
import org.example.backend.domain.mail.service.EmailService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;

    //티켓 예매 메일
    @PostMapping("/send/{reservationId}")
    public String sendMail(@PathVariable Long reservationId) throws MessagingException {

        String response = emailService.sendTicketMail(reservationId);  // 사용자 정보를 함께 전달

        return "티켓 예매 메일 발송 완료!" + response;
    }


    //예매 취소 메일
    @PostMapping("/cancel/{reservationId}")
    public String sendCancelMail(@PathVariable Long reservationId) throws MessagingException {
        emailService.sendCancelTicketMail(reservationId);
        return "티켓 예매 취소 메일 발송 완료!";
    }
}
