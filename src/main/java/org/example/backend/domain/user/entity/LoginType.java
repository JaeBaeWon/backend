package org.example.backend.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum LoginType {

    LOCAL("로컬"),
    GOOGLE("구글"),
    NAVER("네이버");

    private final String displayName;

}
