package org.example.backend.domain.performance.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.backend.domain.performance.entity.PerformanceCategory;
import org.example.backend.domain.performance.entity.PerformanceStatus;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class PerformanceRankingDto {
    private Long performanceId;
    private String title;
    private PerformanceCategory category;
    private LocalDateTime performanceStartAt;
    private LocalDateTime performanceEndAt;
    private LocalDateTime performanceOpenAt;
    private String location;
    private String performanceImg;
    private int price;
    private Long views;
    private PerformanceStatus performanceStatus;
}

