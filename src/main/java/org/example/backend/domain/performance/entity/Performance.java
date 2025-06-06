package org.example.backend.domain.performance.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.backend.domain.performance.dto.request.PerformanceRequestDto;
import org.example.backend.domain.user.entity.User;


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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public void updateFromDto(PerformanceRequestDto dto) {
        this.title = dto.getTitle();
        this.description = dto.getDescription();
        this.category = dto.getCategory();
        this.performanceCode = dto.getPerformanceCode();
        this.performanceStartAt = dto.getPerformanceStartAt();
        this.performanceEndAt = dto.getPerformanceEndAt();
        this.performanceOpenAt = dto.getPerformanceOpenAt();
        this.location = dto.getLocation();
        this.performanceImg = dto.getPerformanceImg();
        this.price = dto.getPrice();
        this.totalSeats = dto.getTotalSeats();
        this.remainSeats = dto.getTotalSeats();
        this.performanceStatus = dto.getPerformanceStatus();
        this.reservationDay = LocalDateTime.now();
    }
}
