package org.example.backend.global.exception;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.global.exception.dto.ExceptionRes;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ExceptionRes> handleCustomException(CustomException exception) {
        log.warn("CustomException 발생: {}", exception.getMessage());
        return ResponseEntity
                .status(exception.getContent().getHttpStatus())
                .body(ExceptionRes.of(exception.getContent().getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionRes> handleInputFieldException(MethodArgumentNotValidException e) {
        FieldError mainError = e.getFieldErrors().get(0);
        String[] errorInfo = Objects.requireNonNull(mainError.getDefaultMessage()).split(":");
        String message = errorInfo[0];

        log.warn("입력 필드 검증 예외 발생: {} (field: {}, rejectedValue: {})",
                message, mainError.getField(), mainError.getRejectedValue());

        return ResponseEntity
                .badRequest()
                .body(new ExceptionRes(message));
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionRes> handleJsonException(HttpMessageNotReadableException e) {
        log.warn("Json 파싱 예외 발생: {}", e.getMessage(), e);

        return ResponseEntity
                .badRequest()
                .body(new ExceptionRes("Json 형식이 올바르지 않습니다."));
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionRes> handleRequestMethodException(HttpRequestMethodNotSupportedException e) {
        log.warn("지원하지 않는 HTTP 메서드 요청: {}", e.getMessage(), e);

        return ResponseEntity
                .badRequest()
                .body(new ExceptionRes("해당 요청에 대한 API가 존재하지 않습니다. 엔드 포인트를 확인해주시길 바랍니다."));
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionRes> handleUsernameNotFoundException(UsernameNotFoundException e) {
        log.warn("UsernameNotFoundException: {}", e.getMessage());
        return ResponseEntity
                .status(401)
                .body(new ExceptionRes("존재하지 않거나 탈퇴된 사용자입니다."));
    }

    @ExceptionHandler
    public ResponseEntity<ExceptionRes> unhandledException(Exception e, HttpServletRequest request) {
        log.error("UnhandledException: {} {} | errMessage={} | exception={}",
                request.getMethod(),
                request.getRequestURI(),
                e.getMessage(),
                e.getClass().getSimpleName(),
                e);

        return ResponseEntity
                .internalServerError()
                .body(new ExceptionRes("예상하지 못한 오류가 발생했습니다. 백엔드 팀에 문의바랍니다."));
    }
}
