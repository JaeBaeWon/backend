package org.example.backend.domain.reservation.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.backend.domain.payment.entity.PayType;
import org.example.backend.domain.payment.entity.Payment;
import org.example.backend.domain.payment.entity.PaymentStatus;
import org.example.backend.domain.performance.entity.Performance;
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
    private PerformanceStatus performanceStatus;

    private PayType payType;
    private PaymentStatus paymentStatus;
    private Long seatId;
    private String seatInfo;
    private Integer paymentAmount;
    private ReservationStatus refundStatus;
    private Integer refundAmount;

    // ✅ 추가
    private String showTitle;             // 공연 제목
    private String showImage;             // 공연 포스터 URL

    private Long paymentId; // ✅ 환불 요청 시 필요

    public static ReservationDetailsDto of(Reservation reservation, Payment payment) {
        Performance performance = reservation.getPerformanceId();

        return ReservationDetailsDto.builder()
                .userName(reservation.getUserId().getUsername())
                .ticketId(reservation.getTicketId())
                .performanceStartAt(performance.getPerformanceStartAt())
                .location(performance.getLocation())
                .reservationDay(reservation.getReservationDate())
                .performanceStatus(performance.getPerformanceStatus())
                .payType(payment.getPayType())
                .paymentStatus(payment.getPaymentStatus())
                .seatId(reservation.getSeatId().getSeatId())
                .seatInfo(reservation.getSeatId().getSeatSection() + "구역 " + reservation.getSeatId().getSeatNum() + "번")
                .paymentAmount(payment.getPaymentAmount())
                .refundStatus(reservation.getReservationStatus())
                .refundAmount(payment.getRefundAmount())
                .showTitle(performance.getTitle())                  // 🔹 추가
                .showImage(performance.getPerformanceImg())        // 🔹 추가
                .paymentId(payment.getPaymentId())
                .build();
    }
}
