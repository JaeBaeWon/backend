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
public class PerformRes {

    private Long performId;

    private String title;

    private PerformanceCategory category;

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

    private int remainSeats;

    private PerformanceStatus performanceStatus;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime reservationDay;

    public static PerformRes of(Performance performance) {
        return PerformRes.builder()
                .performId(performance.getPerformanceId())
                .title(performance.getTitle())
                .category(performance.getCategory())
                .performStartAt(performance.getPerformanceStartAt())
                .performEndAt(performance.getPerformanceEndAt())
                .location(performance.getLocation())
                .performImg(performance.getPerformanceImg())
                .price(performance.getPrice())
                .views(performance.getViews())
                .remainSeats(performance.getRemainSeats())
                .performanceStatus(performance.getPerformanceStatus())
                .reservationDay(performance.getReservationDay())
                .build();
    }
}
