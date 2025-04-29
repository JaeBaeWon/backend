package org.example.backend.domain.performance.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PerformanceStatus {
    UPCOMING("열릴 예정"),
    ONGOING("이미 열림"),
    CLOSED("마감");

    private final String description;

}
