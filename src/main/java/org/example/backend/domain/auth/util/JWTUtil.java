package org.example.backend.domain.auth.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtil {

    private final SecretKey secretKey;

    // 🔐 시크릿 키 초기화 (환경변수에서 직접 주입)
    public JWTUtil(@Value("${JWT_SECRET}") String secret) {
        if (secret == null || secret.isEmpty()) {
            throw new IllegalStateException("❌ JWT_SECRET 환경변수가 설정되지 않았습니다.");
        }
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), SignatureAlgorithm.HS256.getJcaName());
    }

    // ✅ Access Token 생성 (30분 유효)
    public String createAccessToken(Long userId, String email, String role) {
        return createJwt(userId, email, role, 1000L * 60 * 30); // 30분
    }

    // ✅ Refresh Token 생성 (3일 유효)
    public String createRefreshToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000L * 60 * 60 * 24 * 3)) // 3일
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // ✅ JWT 공통 생성 메서드
    public String createJwt(Long userId, String email, String role, Long expiredMs) {
        return Jwts.builder()
                .claim("userId", userId)
                .claim("email", email)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    // ✅ 토큰 유효성 검증
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // 사용자 pk id 추출
    public Long getUserId(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("userId", Long.class);
    }

    // ✅ 이메일 추출
    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("email", String.class);
    }

    // ✅ 역할(Role) 추출
    public String extractRole(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("role", String.class);
    }

    // ✅ 만료 여부 확인
    public boolean isExpired(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration()
                .before(new Date());
    }

    // ✅ JWTFilter 호환 메서드 추가
    public String getLoginId(String token) {
        return extractEmail(token);
    }

    public String getRole(String token) {
        return extractRole(token);
    }
}
