package org.example.backend.domain.auth.config;

import lombok.RequiredArgsConstructor;
import org.example.backend.domain.auth.util.OnboardingFilter;
import org.example.backend.domain.auth.util.JWTFilter;
import org.example.backend.domain.auth.util.JWTUtil;
import org.example.backend.domain.auth.service.CustomAuthorizationRequestResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.OAuth2AuthorizationRequestResolver;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JWTUtil jwtUtil;
    private final ClientRegistrationRepository clientRegistrationRepository;
    private final JWTFilter jwtFilter;
    private final OnboardingFilter onboardingFilter;

    private final CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.cors(cors -> cors.configurationSource(corsConfigurationSource));

        http.cors(withDefaults());

        // ✅ 권한 제어
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/auth/login", "/auth/join", "/auth/refresh",
                        "/auth/find-id/**", "/auth/reset-password/**", "/auth/check/**",
                        "/oauth2/**",  "health", "/error", "/performance/**",
                        "/auth/check-duplicate", "/seat/**", "/actuator/**"
                ).permitAll()
                .anyRequest().authenticated()
        );

        http.formLogin(auth -> auth.disable());

        // ✅ OAuth2 로그인 설정
        OAuth2AuthorizationRequestResolver customResolver =
                new CustomAuthorizationRequestResolver(clientRegistrationRepository, "/oauth2/authorization");

        http.oauth2Login(auth -> auth
                .loginPage("/auth/login")
                .authorizationEndpoint(config -> config.authorizationRequestResolver(customResolver))
                .defaultSuccessUrl("/")
                .failureUrl("/auth/login?error=true")
                .permitAll()
        );

        http.logout(auth -> auth
                .logoutUrl("/logout")
                .logoutSuccessUrl("/auth/login")
                .permitAll()
        );

        http.csrf(csrf -> csrf.disable());

        // ✅ 필터 순서 등록: JWTFilter → OnboardingFilter
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(onboardingFilter, JWTFilter.class);

        return http.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
