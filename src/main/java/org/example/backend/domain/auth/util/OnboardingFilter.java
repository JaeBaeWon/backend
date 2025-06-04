package org.example.backend.domain.auth.util;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.domain.user.entity.User;
import org.example.backend.domain.user.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class OnboardingFilter extends OncePerRequestFilter {

    private final UserRepository userRepository;

    private static final String[] EXCLUDED_PATHS = {
            "/auth/onboarding", "/auth/login", "/auth/refresh", "/auth/join", "/auth/check",
            "/css", "/js", "/images", "/favicon", "/error", "/index.html"
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String uri = request.getRequestURI();
        for (String path : EXCLUDED_PATHS) {
            if (uri.startsWith(path)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
            Object principal = auth.getPrincipal();
            String email = null;

            if (principal instanceof UserDetails userDetails) {
                email = userDetails.getUsername();
            } else if (principal instanceof OAuth2User oAuth2User) {
                Object attr = oAuth2User.getAttribute("email");
                email = attr != null ? attr.toString() : null;
            }

            if (email != null) {
                User user = userRepository.findByEmail(email).orElse(null);
                if (user != null && !user.isOnboardingCompleted()) {
                    response.sendRedirect(request.getContextPath() + "/auth/onboarding");
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }
}
