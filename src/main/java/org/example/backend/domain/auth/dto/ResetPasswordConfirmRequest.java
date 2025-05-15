package org.example.backend.domain.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordConfirmRequest {
    private String email;
    private String phone;
    private String code;
    private String newPassword;
}
