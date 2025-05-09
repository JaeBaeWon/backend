package org.example.backend.domain.payment.dto;

import lombok.Getter;
import lombok.Setter;
import org.example.backend.domain.payment.entity.PayType;
import org.example.backend.domain.payment.entity.PaymentStatus;

@Getter
@Setter
public class PaymentDto {
    private Long reservationId;
    private int paymentAmount;
    private PayType payType;
    private PaymentStatus paymentStatus;
}
