package org.example.backend.domain.seat.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import org.example.backend.domain.seat.entity.Seat;
import org.example.backend.domain.seat.entity.SeatStatus;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
public class SeatStatusDto implements Serializable {

    private Long seatId;

    private String seatSection;

    private String seatNum;

    private SeatStatus seatStatus;

    private Long performId;

    public static SeatStatusDto of(Seat seat) {
        return SeatStatusDto.builder()
                .seatId(seat.getSeatId())
                .seatSection(seat.getSeatSection())
                .seatNum(seat.getSeatNum())
                .seatStatus(seat.getSeatStatus())
                .performId(seat.getPerformance().getPerformanceId())
                .build();
    }

}

