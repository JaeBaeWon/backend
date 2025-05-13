package org.example.backend.domain.reservation.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter

public class ReservationDto {
    private Long userId;
    private Long performanceId;
    private Long seatId;
}
