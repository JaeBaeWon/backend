package org.example.backend.domain.mail.controller;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.example.backend.domain.auth.config.CustomSecurityUserDetails;
import org.example.backend.domain.mail.dto.EmailDto;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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
    private final JavaMailSender mailSender;

    @PostMapping("/send/test")
    public String testMail() throws Exception {
        MimeMessage msg = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");

        helper.setFrom("podopicker@gmail.com", "Podopicker");
        helper.setTo("내 이메일 주소");  // 테스트용으로 개인 이메일
        helper.setSubject("테스트 메일입니다.");
        helper.setText("<b>테스트 본문입니다.</b>", true);

        mailSender.send(msg);
        return "✅ 발송 요청 완료!";
    }


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
