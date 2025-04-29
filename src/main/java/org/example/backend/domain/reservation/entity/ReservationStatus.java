package org.example.backend.domain.reservation.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ReservationStatus {

    RESERVED("예매"),
    CANCELED("예매 취소");

    private final String description;

}
