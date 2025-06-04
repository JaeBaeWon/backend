package org.example.backend.domain.reservation.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.backend.domain.performance.entity.Performance;
import org.example.backend.domain.seat.entity.Seat;
import org.example.backend.domain.user.entity.User;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservationId;

    private String ticketId;

    @Enumerated(EnumType.STRING)
    private ReservationStatus reservationStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDateTime reservationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id")
    private Performance performance;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seat_id")
    private Seat seat;

    // 기존 메서드
    public void updateReservationStatus(ReservationStatus status) {
        this.reservationStatus = status;
    }

    // ✅ PaymentService에서 호출되는 메서드 대응용
    public void updateStatus(ReservationStatus status) {
        this.reservationStatus = status;
    }

}
