package org.example.backend.domain.notification.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.domain.notification.dto.NotificationDto;
import org.example.backend.domain.notification.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @PostMapping("/subscribe")
    public ResponseEntity<String> subscribe(@RequestBody NotificationDto dto) {
        notificationService.subscribeNotification(dto.getUserId(), dto.getPerformanceId());
        return ResponseEntity.ok("알림 등록이 완료되었습니다.");
    }
}
