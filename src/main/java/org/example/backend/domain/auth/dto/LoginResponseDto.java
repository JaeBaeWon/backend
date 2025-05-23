package org.example.backend.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class  LoginResponseDto {
    private String email;
    private String userName;
    private String role;
    private String accessToken;
    private String refreshToken;
}
