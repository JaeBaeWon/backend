package org.example.backend.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.backend.domain.user.entity.User;
import org.example.backend.domain.user.entity.UserRole;

@Getter
@Setter
@NoArgsConstructor
public class JoinRequest {

    @NotBlank(message = "ID를 입력하세요.")
    private String email;

    @NotBlank(message = "비밀번호를 입력하세요.")
    private String password;

    @NotBlank(message = "비밀번호 확인을 입력하세요.")
    private String passwordCheck; // Add this field for password confirmation

    @NotBlank(message = "이름을 입력하세요.")
    private String user_name;

    public User toEntity() {
        return User.builder()
                .email(this.email)
                .password(this.password)
                .username(this.user_name)
                .role(UserRole.CONSUMER)
                .build();
    }
}
