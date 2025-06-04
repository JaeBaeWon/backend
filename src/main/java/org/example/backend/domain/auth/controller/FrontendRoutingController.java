package org.example.backend.domain.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontendRoutingController {

    @GetMapping({
            "/", // 메인
            "/login", // 일반 로그인
            "/signup", // 회원가입
            "/find-id", // ID 찾기
            "/findemail", // 이메일 찾기
            "/reset-password", // 비밀번호 재설정
            "/resetpassword", // 같은 목적의 우회 경로
            "/onboarding", // 온보딩 (React 전용)
            "/shows", // 공연 리스트
            "/show/**", // 공연 상세
            "/mypage/**", // 마이페이지
            "/reservation/**", // 예매 관련
            "/openalertcomplete", // 알림 완료
            "/show/ranking" // 랭킹
    })
    public String redirect() {
        return "forward:/index.html";
    }
}
