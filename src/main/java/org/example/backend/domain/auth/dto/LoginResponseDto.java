package org.example.backend.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;
import org.example.backend.domain.user.entity.UserRole;

@Getter
@Builder
public class  LoginResponseDto {
    private String email;
    private String userName;
    private UserRole role;
    private String accessToken;
    private String refreshToken;
}
