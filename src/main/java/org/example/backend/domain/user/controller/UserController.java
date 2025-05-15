package org.example.backend.domain.user.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.backend.domain.auth.dto.MemberDto;
import org.example.backend.domain.auth.util.JWTUtil;
import org.example.backend.domain.auth.service.MemberService;
import org.example.backend.domain.reservation.dto.MyPageReservationDto;
import org.example.backend.domain.reservation.dto.ReservationDetailsDto;
import org.example.backend.domain.user.dto.MemberProfileResponse;
import org.example.backend.domain.user.entity.User;
import org.example.backend.domain.user.repository.UpdateProfileRequest;
import org.example.backend.domain.user.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final MemberService memberService;
    private final JWTUtil jwtUtil;

    private boolean isAuthenticated(Authentication auth) {
        return auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName());
    }

    @GetMapping("/user/{userId}")
    public User getUser(@PathVariable Long userId) {
        return userService.getUserById(userId);
    }

    //내 정보 수정
    @PutMapping("/user/profile")
    public ResponseEntity<String> updateProfile(@RequestBody UpdateProfileRequest request,
                                                Authentication authentication) {
        if (!isAuthenticated(authentication)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        memberService.updateMemberProfile(authentication.getName(), request);
        return ResponseEntity.ok("회원 정보가 수정되었습니다.");
    }

    @GetMapping("/user/profile")
    public ResponseEntity<MemberProfileResponse> getProfile(Authentication authentication) {
        if (!isAuthenticated(authentication)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        MemberProfileResponse profile = memberService.getProfile(authentication.getName());
        return ResponseEntity.ok(profile);
    }


    //마이페이지
    @GetMapping("/user/info")
    public ResponseEntity<?> memberInfo(Authentication auth) {
        if (!isAuthenticated(auth)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        MemberDto member = memberService.getLoginMemberByEmail(auth.getName());
        boolean onboardingComplete = memberService.isOnboardingComplete(member.getEmail());

        return ResponseEntity.ok(Map.of(
                "member", member,
                "onboardingComplete", onboardingComplete
        ));
    }

    @GetMapping("/user/reservation")
    public ResponseEntity<List<MyPageReservationDto>> reservationPage(Authentication auth) {
        if (!isAuthenticated(auth)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        List<MyPageReservationDto> reservations = memberService.getReservationsForUser(auth.getName());
        return ResponseEntity.ok(reservations);
    }

    @GetMapping("/user/reservation/{id}")
    public ResponseEntity<ReservationDetailsDto> reservationDetailPage(@PathVariable("id") Long reservationId,
                                                                       Authentication auth) {
        if (!isAuthenticated(auth)) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        ReservationDetailsDto dto = memberService.getReservationByIdAndLoginId(reservationId, auth.getName());
        return ResponseEntity.ok(dto);
    }


    @GetMapping("/test-token")
    public ResponseEntity<?> testToken(HttpServletRequest request) {
        // 1. Authorization 헤더에서 토큰 추출
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization 헤더가 없거나 형식이 잘못되었습니다.");
        }

        String token = authHeader.substring(7); // "Bearer " 제거

        // 2. 이메일 추출
        try {
            Long userId = jwtUtil.getUserId(token);
            System.out.println("🎯 추출된 pk: " + userId);
            return ResponseEntity.ok("추출된 pk: " + userId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("토큰 파싱 오류: " + e.getMessage());
        }
    }

}
