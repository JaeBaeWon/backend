package org.example.backend.domain.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.backend.domain.payment.entity.PayType;
import org.example.backend.domain.payment.entity.Payment;
import org.example.backend.domain.payment.entity.PaymentStatus;
import org.example.backend.domain.performance.entity.PerformanceStatus;
import org.example.backend.domain.reservation.entity.Reservation;
import org.example.backend.domain.reservation.entity.ReservationStatus;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDetailsDto {

    private String userName;               // 예매자
    private String ticketId;              // 예매번호
    private LocalDateTime performanceStartAt; // 이용일
    private String location;              // 장소
    private LocalDateTime reservationDay; // 예매일
    private PerformanceStatus performanceStatus;     // 현재 상태

    private PayType payType; // DTO 내부
    private PaymentStatus paymentStatus;         // 결제상태

    private Long seatId;
    private Integer paymentAmount;        // 가격 / 총 결제금액
    private ReservationStatus refundStatus;         // 취소여부 (true: 환불됨)
    private Integer refundAmount;         // 환불 금액

    public static ReservationDetailsDto of(Reservation reservation, Payment payment) {
        return ReservationDetailsDto.builder()
                .userName(reservation.getUser().getUsername())
                .ticketId(reservation.getTicketId())
                .performanceStartAt(reservation.getPerformance().getPerformanceStartAt())
                .location(reservation.getPerformance().getLocation()) // 공연장명
                .reservationDay(reservation.getReservationDate())
                .performanceStatus(reservation.getPerformance().getPerformanceStatus())
                .payType(payment.getPayType())
                .paymentStatus(payment.getPaymentStatus())
                .seatId(reservation.getSeat().getSeatId()) // 예: "A10"
                .paymentAmount(payment.getPaymentAmount())
                .refundStatus(reservation.getReservationStatus()) // ✅ enum 값을 그대로 넘김
                .refundAmount(payment.getRefundAmount())
                .build();
    }
}
