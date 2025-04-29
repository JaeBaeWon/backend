package org.example.backend.domain.refund.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.backend.domain.payment.entity.Payment;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
public class Refund {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long refundId;

    private int refundAmount;

    @Enumerated(EnumType.STRING)
    private RefundStatus refundStatus;

    private String refundReason;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    private Payment payment;
}
