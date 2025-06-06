package org.example.backend.domain.performance.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.example.backend.domain.performance.entity.PerformanceCategory;
import org.example.backend.domain.performance.entity.PerformanceStatus;

import java.time.LocalDateTime;

@Getter
@Setter
public class PerformanceRequestDto {
    private String title;
    private String description;
    private PerformanceCategory category;
    private String performanceCode;
    private LocalDateTime performanceStartAt;
    private LocalDateTime performanceEndAt;
    private LocalDateTime performanceOpenAt;
    private String location;
    private int price;
    private int totalSeats;
    private PerformanceStatus performanceStatus;
}