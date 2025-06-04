package org.example.backend.domain.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.backend.domain.payment.entity.Payment;
import org.example.backend.domain.reservation.entity.Reservation;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ReservationMessageDto {
    private Long userId;
    private Long performanceId;
    private Long seatId;

    private String ticketId;

    private String impUid;
    private String merchantUid;
    private int paymentAmount;
    private String payMethod;

    public static ReservationMessageDto of(Reservation reservation, Payment payment) {
        return ReservationMessageDto.builder()
                .userId(reservation.getUserId().getUserId())
                .performanceId(reservation.getPerformanceId().getPerformanceId())
                .seatId(reservation.getSeatId().getSeatId())
                .ticketId(reservation.getTicketId())
                .impUid(payment.getImpUid())
                .merchantUid(payment.getMerchantUid())
                .paymentAmount(payment.getPaymentAmount())
                .payMethod(payment.getPayType().name())
                .build();
    }
}

