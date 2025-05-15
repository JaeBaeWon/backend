package org.example.backend.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.backend.domain.user.entity.User;

@Builder
@Getter
@Setter
public class MemberDto {
    private Long id;
    private String email;
    private String userName;  // user_name 필드 매핑용

    public static MemberDto of(User user) {
        return MemberDto.builder()
                .id(user.getUserId())
                .email(user.getEmail())
                .userName(user.getUsername())
                .build();
    }
}
