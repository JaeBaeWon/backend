package org.example.backend.domain.performance.entity;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PerformanceCategory {
    CONCERT("콘서트"),
    MUSICAL("뮤지컬"),
    PLAY("연극"),
    EXHIBITION("전시");

    private final String displayName;

}
