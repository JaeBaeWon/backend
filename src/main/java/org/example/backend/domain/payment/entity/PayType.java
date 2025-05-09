package org.example.backend.domain.payment.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PayType {

    KAKAOPAY("카카오페이"),
    CARD("신용카드"),
    POINT("포인트");

    private final String description;
}
