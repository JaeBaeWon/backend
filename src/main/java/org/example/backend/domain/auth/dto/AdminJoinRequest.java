package org.example.backend.domain.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.backend.domain.user.entity.User;
import org.example.backend.domain.user.entity.UserRole;

@Getter
@Setter
@NoArgsConstructor
public class AdminJoinRequest {
    private String email;
    private String password;
    private String passwordCheck;
    private String user_name;

    private String role; // MANAGER 직접 입력

    public User toEntity() {
        return User.builder()
                .email(this.email)
                .password(this.password)
                .username(this.user_name)
                .role(UserRole.valueOf(role)) // Postman에서 "MANAGER" 입력
                .build();
    }
}
