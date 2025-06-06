package org.example.backend.global.exception;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

    private final ExceptionContent content;

    // ✅ 문자열 메시지를 직접 받을 수 있도록 생성자 추가
    public CustomException(String message) {
        super(message);
        this.content = null; // 명시적으로 초기화
    }

    public CustomException(ExceptionContent content) {
        super(content.getMessage());
        this.content = content;
    }
}
