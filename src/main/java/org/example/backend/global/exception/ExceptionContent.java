package org.example.backend.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum ExceptionContent {
    INVALID_TOKEN(UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(UNAUTHORIZED, "만료된 토큰입니다."),
    LOGIN_FAILURE(UNAUTHORIZED, "로그인에 실패했습니다."),
    NOT_AUTHENTICATION(UNAUTHORIZED, "인증이 필요합니다."),

    NOT_AUTHORIZATION(FORBIDDEN, "권한이 필요합니다."),

    BAD_REQUEST_SIGNUP(BAD_REQUEST, "회원가입에 실패했습니다. 이미 가입한` 이메일입니다."),

    NOT_FOUND_USER(NOT_FOUND, "존재하지 않는 사용자입니다."),

    NOT_FOUND_MEMBER(NOT_FOUND, "유효하지 않은 회원입니다."),

    NOT_FOUND_PERFORMANCE(NOT_FOUND, "유효하지 않은 공연입니다."),

    NOT_FOUND_SEAT(NOT_FOUND, "유효하지 않은 좌석입니다."),

    NOT_FOUND_RESERVATION(NOT_FOUND, "유효하지 않은 예매입니다."),
    
    NOT_FOUND_PAYMENT(NOT_FOUND, "유효하지 않은 결제 내역입니다."),

    ALREADY_RESERVED(FORBIDDEN, "이미 예약된 좌석입니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
