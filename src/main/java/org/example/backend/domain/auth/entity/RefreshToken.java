package org.example.backend.domain.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.backend.domain.user.entity.User;
import java.time.LocalDateTime;

import org.example.backend.domain.user.entity.User;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    @Column(columnDefinition = "TEXT")
    private String token;

    private LocalDateTime expiration;

    // ✅ 실용적인 생성자 추가 (id 없이 email/token만 받는 용도)
    public RefreshToken(String email, String token) {
        this.email = email;
        this.token = token;
        this.expiration = LocalDateTime.now().plusDays(3); // 기본 3일 유효
    }

    public RefreshToken(User user, String token, LocalDateTime expiration) {
        this.email = user.getEmail(); // user로부터 이메일 추출
        this.token = token;
        this.expiration = expiration;
    }
}
