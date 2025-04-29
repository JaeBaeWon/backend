package org.example.backend.domain.payment.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PaymentStatus {

    COMPLETED("결제 완료"),
    FAILED("결제 실패"),
    CANCELED("결제 취소");

    private final String description;
}
