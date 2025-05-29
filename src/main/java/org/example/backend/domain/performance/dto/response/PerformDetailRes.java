package org.example.backend.domain.performance.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.backend.domain.performance.entity.Performance;
import org.example.backend.domain.performance.entity.PerformanceCategory;
import org.example.backend.domain.performance.entity.PerformanceStatus;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class PerformDetailRes {

    private Long performId;

    private String title;

    private String description;

    private PerformanceCategory category;

    private String performCode;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime performStartAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime performEndAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime performanceOpenAt;

    private String location;

    private String performImg;

    private int price;

    private Long views;

    private int totalSeats;

    private int remainSeats;

    private PerformanceStatus performanceStatus;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime reservationDay;

    public static PerformDetailRes of(Performance performance, long remainSeats) {
        return PerformDetailRes.builder()
                .performId(performance.getPerformanceId())
                .title(performance.getTitle())
                .description(performance.getDescription())
                .category(performance.getCategory())
                .performCode(performance.getPerformanceCode())
                .performStartAt(performance.getPerformanceStartAt())
                .performEndAt(performance.getPerformanceEndAt())
                .performanceOpenAt(performance.getPerformanceOpenAt())
                .location(performance.getLocation())
                .performImg(performance.getPerformanceImg())
                .price(performance.getPrice())
                .views(performance.getViews())
                .totalSeats(performance.getTotalSeats())
                .remainSeats((int) remainSeats)
                .performanceStatus(performance.getPerformanceStatus())
                .reservationDay(performance.getReservationDay())
                .build();
    }
}
