package org.example.backend.domain.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.backend.domain.performance.entity.Performance;
import org.example.backend.domain.performance.entity.PerformanceStatus;
import org.example.backend.domain.reservation.entity.Reservation;
import org.example.backend.domain.reservation.entity.ReservationStatus;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MyPageReservationDto {

    private Long reservationId;
    private String ticketId;                // 예약 번호
    private String title;                 // 공연명
    private LocalDateTime reservationDay; // 예매일
    private LocalDateTime performanceStartAt; // 관람일
    private PerformanceStatus performanceStatus;
    // 공연 상세 페이지 링크를 구성할 수 있는 필드 (선택 사항)
    private Long performanceId; // 상세 페이지 조회용 ID

    private ReservationStatus reservationStatus;

    public static MyPageReservationDto of(Reservation reservation, Performance performance) {
        return MyPageReservationDto.builder()
                .reservationId(reservation.getReservationId())
                .ticketId(reservation.getTicketId()) // reservation.getReservationId() 도 가능
                .title(reservation.getPerformanceId().getTitle())
                .reservationDay(reservation.getReservationDate())
                .performanceStartAt(performance.getPerformanceStartAt())
                .performanceStatus(performance.getPerformanceStatus())
                .performanceId(performance.getPerformanceId())
                .reservationStatus(reservation.getReservationStatus())
                .build();
    }

}
