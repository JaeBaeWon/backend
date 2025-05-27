package org.example.backend.domain.manage.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.backend.domain.performance.entity.PerformanceCategory;
import org.example.backend.domain.performance.entity.PerformanceStatus;

import java.time.LocalDateTime;

@Getter
@Setter
public class ManageRequestDto {
    private String title;
    private String description;
    private PerformanceCategory category;
    private String performanceCode;
    private LocalDateTime performanceStartAt;
    private LocalDateTime performanceEndAt;
    private LocalDateTime performanceOpenAt;
    private String location;
    private String performanceImg;
    private int price;
    private int totalSeats;
    private PerformanceStatus performanceStatus;
}
