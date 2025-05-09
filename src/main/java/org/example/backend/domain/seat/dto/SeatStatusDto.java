package org.example.backend.domain.seat.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import org.example.backend.domain.seat.entity.Seat;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
@ToString
public class SeatStatusDto implements Serializable {

    private Long seatId;

    private String seatSection;

    private String seatNum;

    private boolean seatReserved;

    private Long performId;

    public static SeatStatusDto of(Seat seat, boolean seatReserved) {
        return SeatStatusDto.builder()
                .seatId(seat.getSeatId())
                .seatSection(seat.getSeatSection())
                .seatNum(seat.getSeatNum())
                .seatReserved(seatReserved)
                .performId(seat.getPerformance().getPerformId())
                .build();
    }

}

