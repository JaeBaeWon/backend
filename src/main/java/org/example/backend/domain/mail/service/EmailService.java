package org.example.backend.domain.mail.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.backend.domain.notification.entity.Notification;
import org.example.backend.domain.payment.entity.Payment;
import org.example.backend.domain.payment.repository.PaymentRepository;
import org.example.backend.domain.performance.entity.Performance;
import org.example.backend.domain.reservation.entity.Reservation;
import org.example.backend.domain.reservation.repository.ReservationRepository;
import org.example.backend.domain.seat.entity.Seat;
import org.example.backend.domain.user.entity.User;
import org.example.backend.global.exception.CustomException;
import org.example.backend.global.exception.ExceptionContent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.JavaMailSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.text.SimpleDateFormat; // SimpleDateFormat
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import jakarta.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Value("${spring.mail.properties.mail.smtp.name}")
    private String senderName;

    private final JavaMailSender javaMailSender;
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;

    private String readTemplate(String path) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource(path).getInputStream(), StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            throw new RuntimeException("❌ 이메일 템플릿 읽기 실패: " + path, e);
        }
    }

    public String sendTicketMail(Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ExceptionContent.NOT_FOUND_RESERVATION));
        User user = reservation.getUser();
        Performance performance = reservation.getPerformance();
        Seat seat = reservation.getSeat();
        Payment payment = paymentRepository.findByReservation(reservation)
                .orElseThrow(() -> new CustomException(ExceptionContent.NOT_FOUND_PAYMENT));

        try {
            // 템플릿 불러오기
            String html = readTemplate("templates/ticket-success.html")
                    .replace("{username}", user.getUsername())
                    .replace("{title}", performance.getTitle())
                    .replace("{startAt}", performance.getPerformanceStartAt().toString())
                    .replace("{endAt}", performance.getPerformanceEndAt().toString())
                    .replace("{location}", performance.getLocation())
                    .replace("{seat}", seat.getSeatSection() + " 구역, " + seat.getSeatNum() + "번")
                    .replace("{paymentDate}", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(payment.getPaymentDate()))
                    .replace("{paymentAmount}", String.format("%,d", payment.getPaymentAmount()));

            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(new InternetAddress(senderEmail, senderName));
            helper.setTo(user.getEmail());
            helper.setSubject("[티켓 예매 완료] " + performance.getTitle() + " 공연");
            helper.setText(html, true);

            javaMailSender.send(message);
            System.out.println("✅ 메일 전송 성공: " + user.getEmail());

        } catch (Exception e) {
            System.err.println("❌ 메일 전송 실패: " + e.getMessage());
        }

        return user.getEmail();
    }

    //예매 오픈 알람 메일
    public void sendOpenAlarmMail(Notification notification,
                                  LocalDateTime openDate,
                                  String reservationUrl) throws MessagingException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedDate = openDate.format(formatter);

        // 템플릿 읽기 및 값 치환
        String html = readTemplate("templates/open-alarm.html")
                .replace("{username}", notification.getUser().getUsername())
                .replace("{title}", notification.getPerformance().getTitle())
                .replace("{openDate}", formattedDate)
                .replace("{url}", reservationUrl);

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        try {
            helper.setFrom(new InternetAddress(senderEmail, senderName));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        helper.setTo(notification.getUser().getEmail());
        helper.setSubject("[공연 오픈 알림] " + notification.getPerformance().getTitle() + " 공연 예매 시작 안내");
        helper.setText(html, true);

        javaMailSender.send(message);
    }


    //예매 취소 메일
    public void sendCancelTicketMail(Long reservationId) throws MessagingException {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new CustomException(ExceptionContent.NOT_FOUND_RESERVATION));
        User user = reservation.getUser();
        Performance performance = reservation.getPerformance();
        Seat seat = reservation.getSeat();
        Payment payment = paymentRepository.findByReservation(reservation)
                .orElseThrow(() -> new CustomException(ExceptionContent.NOT_FOUND_PAYMENT));

        // 템플릿 읽기 및 값 치환
        String html = readTemplate("templates/ticket-cancel.html")
                .replace("{username}", user.getUsername())
                .replace("{title}", performance.getTitle())
                .replace("{startAt}", performance.getPerformanceStartAt().toString())
                .replace("{endAt}", performance.getPerformanceEndAt().toString())
                .replace("{location}", performance.getLocation())
                .replace("{seat}", seat.getSeatSection() + " 구역, " + seat.getSeatNum() + "번")
                .replace("{paymentDate}", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(payment.getPaymentDate()))
                .replace("{paymentAmount}", String.format("%,d", payment.getPaymentAmount()));

        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        try {
            helper.setFrom(new InternetAddress(senderEmail, senderName));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }

        helper.setTo(user.getEmail());
        helper.setSubject("[티켓 예매 취소] " + performance.getTitle() + " 예매 취소 안내");
        helper.setText(html, true);

        javaMailSender.send(message);
    }

}

