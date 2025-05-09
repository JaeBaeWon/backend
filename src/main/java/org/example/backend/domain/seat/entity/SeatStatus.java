package org.example.backend.domain.seat.entity;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum SeatStatus {
    HOLD("결제 대기"),
    BOOKED("결제 완료"),
    AVAILABLE("선택 가능");

    private final String description;
}
