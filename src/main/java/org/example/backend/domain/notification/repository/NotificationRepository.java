package org.example.backend.domain.notification.repository;

import org.springframework.data.jpa.repository.Query;
import org.example.backend.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    @Query("SELECT n FROM Notification n JOIN FETCH n.user JOIN FETCH n.performance")
    List<Notification> findAllWithUserAndPerformance();
}
