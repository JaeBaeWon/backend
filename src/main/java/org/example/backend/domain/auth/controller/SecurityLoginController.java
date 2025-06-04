package org.example.backend.domain.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.backend.domain.auth.dto.*;
import org.example.backend.domain.auth.entity.RefreshToken;
import org.example.backend.domain.auth.service.MemberService;
import org.example.backend.domain.auth.util.JWTUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class SecurityLoginController {

    private final MemberService memberService;
    private final JWTUtil jwtUtil;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${recaptcha.secret}")
    private String recaptchaSecret;

    private boolean isAuthenticated(Authentication auth) {
        return auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName());
    }

    private boolean verifyRecaptcha(String token) {
        // ✅ 테스트용 토큰일 경우 항상 true 반환 (Postman이나 로컬 테스트 시 사용)
        if ("test".equals(token)) {
            return true;
        }

        // ✅ 실제 Google reCAPTCHA 검증 요청
        String url = "https://www.google.com/recaptcha/api/siteverify?secret=" + recaptchaSecret + "&response=" + token;

        try {
            Map<String, Object> response = restTemplate.postForObject(url, null, Map.class);
            return response != null && Boolean.TRUE.equals(response.get("success"));
        } catch (Exception e) {
            // 예외 발생 시 로그 출력 및 검증 실패 처리
            System.err.println("reCAPTCHA 검증 중 예외 발생: " + e.getMessage());
            return false;
        }
    }

    // ✅ 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        if (!verifyRecaptcha(loginRequest.getRecaptchaToken())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("로봇 검증 실패");
        }

        LoginResponseDto loginResult = memberService.login(loginRequest);
        if (loginResult == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("ID 또는 비밀번호가 틀립니다.");
        }

        ResponseCookie cookie = ResponseCookie.from("refreshToken", loginResult.getRefreshToken())
                .httpOnly(true)
                .secure(false) // teseter 환경에서는 false (http 쓸 때만)
                .path("/")
                .maxAge(3 * 24 * 60 * 60)
                .sameSite("Strict")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());

        return ResponseEntity.ok(Map.of(
                "accessToken", loginResult.getAccessToken(),
                "role", loginResult.getRole(),
                "name", loginResult.getUserName(),
                "onboardingComplete", memberService.isOnboardingComplete(loginResult.getEmail())));
    }

    // ✅ 회원가입
    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody JoinRequest joinRequest) {
        if (memberService.checkLoginIdDuplicate(joinRequest.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 존재하는 ID입니다.");
        }

        if (!joinRequest.getPassword().equals(joinRequest.getPasswordCheck())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("비밀번호가 일치하지 않습니다.");
        }

        memberService.join(joinRequest);
        return ResponseEntity.ok("회원가입 성공");
    }

    // ✅ 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext()
                .getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
            request.getSession().invalidate();
        }
        return ResponseEntity.ok("로그아웃 완료");
    }

    // ✅ 온보딩 정보 제출
    @PostMapping("/onboarding")
    public ResponseEntity<String> submitOnboardingInfo(@RequestBody OnboardingRequest request,
            Authentication authentication) {
        if (!isAuthenticated(authentication)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String email = null;
        Object principal = authentication.getPrincipal();

        if (principal instanceof org.springframework.security.core.userdetails.UserDetails userDetails) {
            email = userDetails.getUsername(); // 로컬 로그인
        } else if (principal instanceof org.springframework.security.oauth2.core.user.OAuth2User oAuth2User) {
            email = oAuth2User.getAttribute("email"); // OAuth2 로그인
        }

        if (email == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("이메일을 가져올 수 없습니다.");
        }

        memberService.updateOnboardingInfo(
                email,
                String.valueOf(request.getGender()),
                request.getZipCode(),
                request.getStreetAdr(),
                request.getDetailAdr(),
                request.getPhone(),
                request.getBirthDate());

        return ResponseEntity.ok("온보딩 완료");
    }

    // ✅ ID 찾기 - 인증번호 전송
    @PostMapping("/find-id/send-code")
    public ResponseEntity<String> sendCodeForFindId(@RequestBody FindIdRequest request) {
        String result = memberService.sendCertificationNumberForIdFind(request);
        return ResponseEntity.ok(result);
    }

    // ✅ ID 찾기 - 인증번호 검증
    @PostMapping("/find-id/verify-code")
    public ResponseEntity<FindIdResponseDto> verifyCode(@RequestBody SmsVerifyIdRequest request) {
        FindIdResponseDto result = memberService.verifyCodeAndFindId(request);
        return ResponseEntity.ok(result);
    }

    // ✅ 비밀번호 재설정 - 인증번호 전송
    @PostMapping("/reset-password/send-code")
    public ResponseEntity<String> sendResetCode(@RequestBody ResetPasswordRequest request) {
        String msg = memberService.sendCertificationNumberForReset(
                request.getEmail(), request.getPhone(), request.getBirthday());
        return ResponseEntity.ok(msg);
    }

    // ✅ 비밀번호 재설정
    @PostMapping("/reset-password")
    public ResponseEntity<PasswordResetResponseDto> resetPassword(@RequestBody ResetPasswordRequest request) {
        PasswordResetResponseDto result = memberService.verifyResetCodeAndChangePassword(request);
        return ResponseEntity.ok(result);
    }

    // ✅ AccessToken 재발급
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(
            @CookieValue(name = "refreshToken", required = false) String refreshToken) {
        if (refreshToken == null || !jwtUtil.validateToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or missing token");
        }

        Long userId = jwtUtil.getUserId(refreshToken);

        String email = jwtUtil.extractEmail(refreshToken);
        RefreshToken stored = memberService.getRefreshTokenByEmail(email);

        if (!stored.getToken().equals(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token mismatch");
        }

        if (stored.getExpiration().isBefore(java.time.LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token expired");
        }

        String newAccessToken = jwtUtil.createAccessToken(userId, email, "ROLE_USER");
        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

    // ✅ 이메일 중복 확인 API
    @GetMapping("/check-duplicate")
    public ResponseEntity<?> checkDuplicateEmail(@RequestParam("email") String email) {
        boolean exists = memberService.checkLoginIdDuplicate(email);
        return ResponseEntity.ok(Map.<String, Object>of("available", !exists));
    }

}
