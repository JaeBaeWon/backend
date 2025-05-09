package org.example.backend.domain.reservation.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ReservationStatus {

    PENDING("결제 대기중"),    // 결제 전, 결제하기만 누른 상태
    RESERVED("예매"), // 결제 완료
    CANCELED("예매 취소");

    private final String description;

}
