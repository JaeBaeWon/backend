package org.example.backend.domain.mail.service;

import jakarta.mail.MessagingException;
import lombok.Getter;
import org.example.backend.domain.notification.entity.Notification;
import org.example.backend.domain.notification.repository.NotificationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
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
        Date now = new Date();
        List<Notification> notifications = notificationRepository.findAllWithUserAndPerformance();

        for (Notification notification : notifications) {
            if (notification.getPerformance() == null || notification.getUser() == null) {
                continue;
            }

            Date performStartAt = notification.getPerformance().getPerformStartAt();
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
    private boolean isDaysBefore(Date performStartAt, Date now, int days) {
        Calendar targetDate = Calendar.getInstance();
        targetDate.setTime(now);
        targetDate.add(Calendar.DAY_OF_YEAR, days);

        Calendar performanceDate = Calendar.getInstance();
        performanceDate.setTime(performStartAt);

        return targetDate.get(Calendar.YEAR) == performanceDate.get(Calendar.YEAR) &&
                targetDate.get(Calendar.DAY_OF_YEAR) == performanceDate.get(Calendar.DAY_OF_YEAR);
    }
}