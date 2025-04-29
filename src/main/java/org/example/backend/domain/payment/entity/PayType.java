package org.example.backend.domain.payment.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PayType {

    KAKAO_PAY("카카오페이"),
    CREDIT_CARD("신용카드");

    private final String description;
}
