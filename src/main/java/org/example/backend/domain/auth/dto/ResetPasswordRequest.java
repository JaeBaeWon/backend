package org.example.backend.domain.auth.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ResetPasswordRequest {
    private String email;
    private String phone;
    private LocalDate birthday;
    private String code;
    private String newPassword;
    private String newPasswordCheck;
}
