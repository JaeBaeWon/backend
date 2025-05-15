package org.example.backend.domain.reservation.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.backend.domain.payment.entity.PaymentStatus;
import org.example.backend.domain.performance.entity.Performance;
import org.example.backend.domain.seat.entity.Seat;
import org.example.backend.domain.user.entity.User;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;

    private String ticketId; // 예매 번호 등 고유 식별자 (String)

    @Enumerated(EnumType.STRING)
    private ReservationStatus reservationStatus;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus;

    private LocalDateTime reservationDate;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id")
    private Performance performance;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id")
    private Seat seat;

    private String seatSection;
    private String seatNum;
    private Boolean seatReserved;

    private Integer paymentAmount;
    private String payType;

    private LocalDateTime paymentDate;

    // 기존 메서드
    public void updateReservationStatus(ReservationStatus status) {
        this.reservationStatus = status;
    }

    // ✅ PaymentService에서 호출되는 메서드 대응용
    public void updateStatus(ReservationStatus status) {
        this.reservationStatus = status;
    }

}
