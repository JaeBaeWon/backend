package org.example.backend.domain.payment.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PaymentStatus {

    SUCCESS("결제 완료"),
    CANCELED("결제 취소");

    private final String description;
}
