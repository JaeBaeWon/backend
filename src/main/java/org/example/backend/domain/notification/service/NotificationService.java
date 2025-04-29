package org.example.backend.domain.notification.service;

import jakarta.mail.MessagingException;
import lombok.Getter;
import org.example.backend.domain.mail.service.EmailService;
import org.example.backend.domain.notification.entity.Notification;
import org.example.backend.domain.notification.repository.NotificationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmailService emailService;

    public NotificationService(NotificationRepository notificationRepository, EmailService emailService) {
        this.notificationRepository = notificationRepository;
        this.emailService = emailService;
    }

    @Scheduled(cron = "0 0 9 * * *")
    public void sendPerformanceNotifications() throws MessagingException {
        LocalDateTime now = LocalDateTime.now();
        List<Notification> notifications = notificationRepository.findAllWithUserAndPerformance();

        for (Notification notification : notifications) {
            if (notification.getPerformance() == null || notification.getUser() == null) {
                continue;
            }

            LocalDateTime performStartAt = notification.getPerformance().getPerformStartAt();
            if (performStartAt == null) {
                continue;
            }

            if (isDaysBefore(performStartAt, now, 7) || isDaysBefore(performStartAt, now, 1)) {
                // 메일 발송
                emailService.sendOpenAlarmMail(
                        notification.getUser().getEmail(),
                        notification.getUser().getUsername(),
                        notification.getPerformance().getTitle(),
                        performStartAt,
                        "https://example.com/reserve/" + notification.getPerformance().getPerformId()
                );
            }
        }
    }

    // 날짜 비교하는 메소드
    private boolean isDaysBefore(LocalDateTime performStartAt, LocalDateTime now, int days) {
        LocalDateTime targetDate = now.plusDays(days);

        return performStartAt.toLocalDate().isEqual(targetDate.toLocalDate());
    }
}
