package org.example.backend.domain.manage.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.backend.domain.performance.entity.PerformanceCategory;
import org.example.backend.domain.performance.entity.PerformanceStatus;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Manage {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long performanceId;

    private String title;
    private String description;

    @Enumerated(EnumType.STRING)
    private PerformanceCategory category;

    private String performanceCode;
    private LocalDateTime performanceStartAt;
    private LocalDateTime performanceEndAt;
    private LocalDateTime performanceOpenAt;
    private String location;
    private String performanceImg;
    private int price;
    private Long views;

    private int totalSeats;
    private int remainSeats;

    @Enumerated(EnumType.STRING)
    private PerformanceStatus performanceStatus;

    private LocalDateTime reservationDay;

    // 🔑 공연 등록한 관리자 ID
    private Long managerId;
}
