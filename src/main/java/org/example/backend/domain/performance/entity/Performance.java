package org.example.backend.domain.performance.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Performance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "performance_id") // ← 반드시 테이블 컬럼 이름과 일치시켜야 함!
    private Long performanceId;

    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private PerformanceCategory category;

    private String performanceCode;

    private LocalDateTime performanceStartAt;

    private LocalDateTime performanceEndAt;

    private String location;

    private String performanceImg;

    private int price;

    private Long views;

    private int totalSeats;

    private int remainSeats;

    @Enumerated(EnumType.STRING)
    private PerformanceStatus performanceStatus;
}
