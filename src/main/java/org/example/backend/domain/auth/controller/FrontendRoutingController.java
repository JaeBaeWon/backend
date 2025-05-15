package org.example.backend.domain.auth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class FrontendRoutingController {

    @GetMapping({
            "/",                      // 루트 페이지
            "/login",
            "/signup",
            "/auth/**",
            "/mypage/**",
            "/reservation/**",
            "/reset-password",
            "/resetpassword",        // 혹시 대소문자나 다른 경로 오타 방지
            "/find-id",
            "/findemail",
            "/onboarding",
            "/shows",                // 리스트 페이지
            "/show/**",              // 상세 페이지 포함
            "/openalertcomplete",
            "/show/ranking"
    })
    public String redirect() {
        return "forward:/index.html";
    }
}
