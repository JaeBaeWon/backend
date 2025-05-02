package org.example.backend.domain.reservation.dto;

import lombok.Builder;
import org.example.backend.domain.payment.dto.PaymentDto;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class ReservationDto {
    private Long userId;
    private PaymentDto paymentDto;
}
