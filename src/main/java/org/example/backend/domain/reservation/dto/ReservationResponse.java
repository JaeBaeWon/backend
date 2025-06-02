package org.example.backend.domain.reservation.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.backend.domain.reservation.entity.Reservation;

@Getter
@Builder
public class ReservationResponse {

    private Long reservationId;

    private String ticketId;

    private Long userId;

    private Long performanceId;

    private Long seatId;

    public static ReservationResponse of(Reservation reservation){
            return ReservationResponse.builder()
                    .reservationId(reservation.getReservationId())
                    .ticketId(reservation.getTicketId())
                    .userId(reservation.getUserId().getUserId())
                    .performanceId(reservation.getPerformanceId().getPerformanceId())
                    .seatId(reservation.getSeatId().getSeatId())
                    .build();
    }
}
