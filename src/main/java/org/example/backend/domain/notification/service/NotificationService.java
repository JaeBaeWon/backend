package org.example.backend.domain.notification.service;

import jakarta.mail.MessagingException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.example.backend.domain.mail.service.EmailService;
import org.example.backend.domain.notification.entity.Notification;
import org.example.backend.domain.notification.repository.NotificationRepository;
import org.example.backend.domain.performance.entity.Performance;
import org.example.backend.domain.performance.repository.PerformanceRepository;
import org.example.backend.domain.user.entity.User;
import org.example.backend.domain.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Service
@RequiredArgsConstructor
public class NotificationService {

    @Value("${spring.mail.username}")
    private String MAIL_ADDRESS;

    @Value("${spring.mail.properties.mail.smtp.name}")
    private String MAIL_NAME;

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final PerformanceRepository performanceRepository;
    private final EmailService emailService;

    public void subscribeNotification(Long userId, Long performanceId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
        Performance performance = performanceRepository.findById(performanceId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid performance ID"));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setPerformance(performance);
        notificationRepository.save(notification);
    }

    @Scheduled(cron = "0 * * * * *")
    public void sendPerformanceNotifications() throws MessagingException {
        LocalDateTime now = LocalDateTime.now();
        List<Notification> notifications = notificationRepository.findAllWithUserAndPerformance();

        for (Notification notification : notifications) {
            if (notification.getPerformance() == null || notification.getUser() == null) {
                continue;
            }

            LocalDateTime performOpenAt = notification.getPerformance().getPerformanceOpenAt();
            if (performOpenAt == null) {
                continue;
            }

            if (isDaysBefore(performOpenAt, now, 10) || isDaysBefore(performOpenAt, now, 1)) {
                // 메일 발송
                emailService.sendOpenAlarmMail(notification, performOpenAt, "https://podopicker.store/"
                );
            }
        }
    }

    // 날짜 비교하는 메소드
    private boolean isDaysBefore(LocalDateTime performOpenAt, LocalDateTime now, int days) {
        LocalDateTime targetDate = now.plusDays(days);

        return performOpenAt.toLocalDate().isEqual(targetDate.toLocalDate());
    }
}
