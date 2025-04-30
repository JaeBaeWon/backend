package org.example.backend.domain.mail.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.JavaMailSender;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.text.SimpleDateFormat; // SimpleDateFormat
import java.time.LocalDateTime;
import java.util.Date; // Date
import jakarta.mail.internet.InternetAddress;
import java.io.UnsupportedEncodingException;

@Service
public class EmailService {

    @Value("${spring.mail.username}")
    private String MAIL_ADDRESS;

    @Value("${spring.mail.properties.mail.smtp.name}")
    private String MAIL_NAME;

	@Autowired
	private JavaMailSender mailSender;

    //티켓 예매 메일
    public void sendTicketMail(String email, String username, String title,
                               String performStartAt, String performEndAt, String location,
                               String seatSection, String seatNum, int paymentAmount,
                               Date paymentDate) throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        String content = "<html><body style='font-family: Arial, sans-serif; padding: 20px;'>"
                + "<div style='max-width: 600px; margin: auto; border: 1px solid #ccc; border-radius: 10px; padding: 20px; background-color: #f9f9f9;'>"
                + "<h2 style='color: #2c3e50;'>🎟️ " + username + "님, 티켓 예매가 완료되었습니다!</h2>"
                + "<hr>"
                + "<p><strong>공연명:</strong> " + title + "</p>"
                + "<p><strong>공연시간:</strong> " + performStartAt + " ~ " + performEndAt + "</p>"
                + "<p><strong>공연장소:</strong> " + location + "</p>"
                + "<p><strong>좌석:</strong> " + seatSection + " 구역, " + seatNum + "번</p>"
                + "<p><strong>결제일시:</strong> " + sdf.format(paymentDate) + "</p>"
                + "<p><strong>결제금액:</strong> " + String.format("%,d", paymentAmount) + "원</p>"
                + "<br><p style='font-size:14px; color:#555;'>즐거운 공연 관람 되세요! 🎶</p>"
                + "</div>"
                + "</body></html>";

        try {
            helper.setFrom(new InternetAddress(MAIL_ADDRESS, MAIL_NAME));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        helper.setTo(email);
        helper.setSubject("[티켓 예매 완료] " + title + " 공연");
        helper.setText(content, true);

        try {
            mailSender.send(message);
            System.out.println("✅ 메일 발송 성공: " + email);
        } catch (Exception e) {
            System.err.println("❌ 메일 발송 실패: " + e.getMessage());
        }
	}

    //예매 오픈 알람 메일
    public void sendOpenAlarmMail(String email, String username, String title,
                                  LocalDateTime openDate, String reservationUrl) throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        String content = "<html><body style='font-family: Arial, sans-serif; padding: 20px;'>"
                + "<div style='max-width: 600px; margin: auto; border: 1px solid #ccc; border-radius: 10px; padding: 20px; background-color: #f9f9f9;'>"
                + "<h2 style='color: #2c3e50;'>🎉 " + username + "님, " + title + " 공연이 오픈됩니다!</h2>"
                + "<hr>"
                + "<p><strong>오픈 일시:</strong> " + sdf.format(openDate) + "</p>"
                + "<p><strong>예매하러 가기:</strong> <a href='" + reservationUrl + "' target='_blank'>여기를 클릭하세요</a></p>"
                + "<br><p style='font-size:14px; color:#555;'>좋은 좌석은 빠르게 마감될 수 있으니 서둘러 주세요!</p>"
                + "</div>"
                + "</body></html>";

        try {
            helper.setFrom(new InternetAddress(MAIL_ADDRESS, MAIL_NAME));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        helper.setTo(email);
        helper.setSubject("[공연 오픈 알림] " + title + " 공연 예매 시작 안내");
        helper.setText(content, true);

        mailSender.send(message);
    }

    //예매 취소 메일
    public void sendCancelTicketMail(String email, String username, String title,
                               String performStartAt, String performEndAt, String location,
                               String seatSection, String seatNum, int paymentAmount,
                               Date paymentDate) throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        String content = "<html><body style='font-family: Arial, sans-serif; padding: 20px;'>"
                + "<div style='max-width: 600px; margin: auto; border: 1px solid #ffcccc; border-radius: 10px; padding: 20px; background-color: #fff0f0;'>"
                + "<h2 style='color: #e74c3c;'>❌ " + username + "님, 티켓 예매가 취소되었습니다.</h2>"
                + "<hr>"
                + "<p><strong>공연명:</strong> " + title + "</p>"
                + "<p><strong>공연시간:</strong> " + performStartAt + " ~ " + performEndAt + "</p>"
                + "<p><strong>공연장소:</strong> " + location + "</p>"
                + "<p><strong>좌석:</strong> " + seatSection + " 구역, " + seatNum + "번</p>"
                + "<p><strong>결제일시:</strong> " + sdf.format(paymentDate) + "</p>"
                + "<p><strong>취소금액:</strong> " + String.format("%,d", paymentAmount) + "원</p>"
                + "<br><p style='font-size:14px; color:#555;'>추후 더 좋은 공연으로 찾아뵙겠습니다. 감사합니다!</p>"
                + "</div>"
                + "</body></html>";

        try {
            helper.setFrom(new InternetAddress(MAIL_ADDRESS, MAIL_NAME));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        helper.setTo(email);
        helper.setSubject("[티켓 예매 취소] " + title + " 예매 취소 안내");
        helper.setText(content, true);

        mailSender.send(message);
    }
}

