package org.example.backend.domain.mail.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.backend.domain.mail.dto.EmailDto;
import org.example.backend.domain.notification.entity.Notification;
import org.example.backend.domain.payment.entity.Payment;
import org.example.backend.domain.payment.repository.PaymentRepository;
import org.example.backend.domain.performance.entity.Performance;
import org.example.backend.domain.reservation.entity.Reservation;
import org.example.backend.domain.reservation.repository.ReservationRepository;
import org.example.backend.domain.seat.entity.Seat;
import org.example.backend.domain.seat.repository.SeatRepository;
import org.example.backend.domain.user.entity.User;
import org.example.backend.domain.user.repository.UserRepository;
import org.example.backend.global.exception.CustomException;
import org.example.backend.global.exception.ExceptionContent;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Date; // Date
import jakarta.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Value("${spring.mail.properties.mail.smtp.name}")
    private String senderName;

    private final JavaMailSender mailSender;
    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final SeatRepository seatRepository;

    private String readTemplate(String path) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new ClassPathResource(path).getInputStream(), StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            throw new RuntimeException("❌ 이메일 템플릿 읽기 실패: " + path, e);
        }
    }

    public void sendTicketMail(Long reservationId) {
        try {
            Reservation reservation = reservationRepository.findById(reservationId)
                    .orElseThrow(() -> new CustomException(ExceptionContent.NOT_FOUND_RESERVATION));
            User user = reservation.getUser();
            Performance p = reservation.getPerformance();
            Payment payment = paymentRepository.findByReservation(reservation)
                    .orElseThrow(() -> new CustomException(ExceptionContent.NOT_FOUND_PAYMENT));
            Seat seat = seatRepository.findByPerformance(p);

            // 템플릿 불러오기
            String html = readTemplate("templates/ticket-success.html")
                    .replace("{username}", user.getUsername())
                    .replace("{title}", p.getTitle())
                    .replace("{startAt}", p.getPerformanceStartAt().toString())
                    .replace("{endAt}", p.getPerformanceEndAt().toString())
                    .replace("{location}", p.getLocation())
                    .replace("{seat}", seat.getSeatSection() + " 구역, " + seat.getSeatNum() + "번")
                    .replace("{paymentDate}", new SimpleDateFormat("yyyy-MM-dd HH:mm").format(payment.getPaymentDate()))
                    .replace("{paymentAmount}", String.format("%,d", payment.getPaymentAmount()));

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(new InternetAddress(senderEmail, senderName));
            helper.setTo(user.getEmail());
            helper.setSubject("[티켓 예매 완료] " + p.getTitle() + " 공연");
            helper.setText(html, true);

            mailSender.send(message);
            System.out.println("✅ 메일 전송 성공: " + user.getEmail());

        } catch (Exception e) {
            System.err.println("❌ 메일 전송 실패: " + e.getMessage());
        }
    }


    //예매 오픈 알람 메일
    public void sendOpenAlarmMail(Notification notification,
                                  LocalDateTime openDate,
                                  String reservationUrl) throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        String username = notification.getUser().getUsername();
        String title = notification.getPerformance().getTitle();
        String email = notification.getUser().getEmail();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedDate = openDate.format(formatter);

        String content = "<html><body style='font-family: Arial, sans-serif; padding: 20px;'>"
                + "<div style='max-width: 600px; margin: auto; border: 1px solid #ccc; border-radius: 10px; padding: 20px; background-color: #f9f9f9;'>"
                + "<h2 style='color: #2c3e50;'>🎉 " + username + "님, " + title + " 공연이 오픈됩니다!</h2>"
                + "<hr>"
                + "<p><strong>오픈 일시:</strong> " + formattedDate + "</p>"
                + "<p><strong>예매하러 가기:</strong> <a href='" + reservationUrl + "' target='_blank'>포도피커 예매사이트</a></p>"
                + "<br><p style='font-size:14px; color:#555;'>좋은 좌석은 빠르게 마감될 수 있으니 서둘러 주세요!</p>"
                + "</div>"
                + "</body></html>";

        try {
            helper.setFrom(new InternetAddress(senderEmail, senderName));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        helper.setTo(email);
        helper.setSubject("[공연 오픈 알림] " + title + " 공연 예매 시작 안내");
        helper.setText(content, true);

        mailSender.send(message);
    }

    //예매 취소 메일
    public void sendCancelTicketMail(EmailDto dto) throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        String username = dto.getUsername();
        String title = dto.getTitle();
        String email = dto.getEmail();
        String performStartAt = dto.getPerformStartAt();
        String performEndAt = dto.getPerformEndAt();
        String location = dto.getLocation();
        String seatSection = dto.getSeatSection();
        String seatNum = dto.getSeatNum();
        int paymentAmount = dto.getPaymentAmount();


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        String content = "<html><body style='font-family: Arial, sans-serif; padding: 20px;'>"
                + "<div style='max-width: 600px; margin: auto; border: 1px solid #ffcccc; border-radius: 10px; padding: 20px; background-color: #fff0f0;'>"
                + "<h2 style='color: #e74c3c;'>❌ " + username + "님, 티켓 예매가 취소되었습니다.</h2>"
                + "<hr>"
                + "<p><strong>공연명:</strong> " + title + "</p>"
                + "<p><strong>공연시간:</strong> " + performStartAt + " ~ " + performEndAt + "</p>"
                + "<p><strong>공연장소:</strong> " + location + "</p>"
                + "<p><strong>좌석:</strong> " + seatSection + " 구역, " + seatNum + "번</p>"
                + "<p><strong>결제일시:</strong> " + sdf.format(dto.getPaymentDate()) + "</p>"
                + "<p><strong>취소금액:</strong> " + String.format("%,d", paymentAmount) + "원</p>"
                + "<br><p style='font-size:14px; color:#555;'>추후 더 좋은 공연으로 찾아뵙겠습니다. 감사합니다!</p>"
                + "</div>"
                + "</body></html>";

        try {
            helper.setFrom(new InternetAddress(senderEmail, senderName));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        helper.setTo(email);
        helper.setSubject("[티켓 예매 취소] " + title + " 예매 취소 안내");
        helper.setText(content, true);

        mailSender.send(message);
    }
}

