package org.example.backend.domain.mail.controller;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.example.backend.domain.auth.config.CustomSecurityUserDetails;
import org.example.backend.domain.mail.dto.EmailDto;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.example.backend.domain.mail.service.EmailService;   // EmailService import
import org.springframework.format.annotation.DateTimeFormat;   // DateTimeFormat import
import java.util.Date;                                         // Date import

//이메일 전송을 담당하는 controller
@RestController
@RequiredArgsConstructor
@RequestMapping("/email")
public class EmailController {

    private final EmailService emailService;

    //티켓 예매 메일
    @PostMapping("/send/{reservationId}")
    public String sendMail(@PathVariable Long reservationId, Authentication auth) throws MessagingException {

        emailService.sendTicketMail(reservationId);  // 사용자 정보를 함께 전달

        return "티켓 예매 메일 발송 완료!";
    }


    //예매 취소 메일
    @PostMapping("/sendCancel")
    public String sendCancelMail(@RequestBody EmailDto dto) throws MessagingException {
        emailService.sendCancelTicketMail(dto);
        return "티켓 예매 취소 메일 발송 완료!";
    }
}
