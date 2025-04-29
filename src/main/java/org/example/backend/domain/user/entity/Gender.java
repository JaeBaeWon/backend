package org.example.backend.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Gender {

    MALE("남성"),
    FEMALE("여성");

    private final String displayName;

}
