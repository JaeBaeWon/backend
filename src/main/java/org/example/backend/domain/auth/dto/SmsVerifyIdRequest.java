package org.example.backend.domain.auth.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SmsVerifyIdRequest {
    private String phone;
    private String code;
    private LocalDate birthday;
}
