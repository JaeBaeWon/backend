package org.example.backend.domain.seat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatStatusMessage {
    private Long performId;
    private String seatSection;
    private String seatNum;
    private boolean seatReserved;
}

