package org.example.backend.domain.refund.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum RefundStatus {

    PENDING("환불 대기"),
    COMPLETED("환불 완료");

    private final String description;

}
