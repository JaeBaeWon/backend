package org.example.backend.domain.seat.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.backend.domain.payment.entity.PaymentStatus;
import org.example.backend.domain.performance.entity.Performance;
import org.example.backend.domain.reservation.entity.ReservationStatus;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@Entity
public class Seat {

    @Id  @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seatId;

    private String seatNum;

    private String seatSection;

    @Enumerated(EnumType.STRING)
    private SeatStatus seatStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id")
    private Performance performance;

    public void updateStatus(SeatStatus status) {
        this.seatStatus = status;
    }
}

