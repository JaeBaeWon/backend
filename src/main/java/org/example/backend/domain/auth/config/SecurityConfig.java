package org.example.backend.domain.auth.config;

import lombok.RequiredArgsConstructor;
import org.example.backend.domain.auth.util.OnboardingFilter;
import org.example.backend.domain.auth.util.JWTFilter;
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

import jakarta.servlet.SessionCookieConfig;

import jakarta.servlet.SessionCookieConfig;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

        private final ClientRegistrationRepository clientRegistrationRepository;
        private final JWTFilter jwtFilter;
        private final OnboardingFilter onboardingFilter;
        private final CorsConfigurationSource corsConfigurationSource;
        private final OAuth2SuccessHandler oAuth2SuccessHandler;

        @Bean
        public WebServerFactoryCustomizer<TomcatServletWebServerFactory> sameSiteCookieConfig() {
                return factory -> factory.addContextCustomizers((Context context) -> {
                        context.setCookieProcessor(new org.apache.tomcat.util.http.Rfc6265CookieProcessor()); // 쿠키 처리기
                                                                                                              // 설정
                        context.addLifecycleListener(event -> {
                                if (event.getType().equals("configure_start")) {
                                        SessionCookieConfig sessionCookieConfig = context.getServletContext()
                                                        .getSessionCookieConfig();
                                        sessionCookieConfig.setHttpOnly(true);
                                        sessionCookieConfig.setSecure(true);
                                        sessionCookieConfig.setName("JSESSIONID");
                                        sessionCookieConfig.setPath("/");
                                        sessionCookieConfig.setDomain("app.podopicker.store");
                                }
                        });
                });
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

                http.cors(cors -> cors.configurationSource(corsConfigurationSource));

                http.authorizeHttpRequests(auth -> auth
                                .requestMatchers(
                                                "/auth/login", "/auth/join", "/auth/refresh",
                                                "/auth/find-id/**", "/auth/reset-password/**", "/auth/check/**",
                                                "/oauth2/**", "/health", "/error", "/performance/**",
                                                "/auth/check-duplicate", "/seat/**", "/actuator/**",
                                                "/auth/onboarding", "/index.html", "/static/**", "/email/**")
                                .permitAll()
                                .anyRequest().authenticated());

                // Spring Form Login 제거 (우리는 SPA 기반 처리)
                http.formLogin(form -> form.disable());

                OAuth2AuthorizationRequestResolver customResolver = new CustomAuthorizationRequestResolver(
                                clientRegistrationRepository, "/oauth2/authorization");

                http.oauth2Login(auth -> auth
                                .authorizationEndpoint(config -> config.authorizationRequestResolver(customResolver))
                                .successHandler(oAuth2SuccessHandler)
                                .failureUrl("/") // ✅ 로그인 실패 시 SPA 진입점으로 이동
                                .permitAll());

                http.logout(auth -> auth
                                .logoutUrl("/logout")
                                .logoutSuccessUrl("/auth/login")
                                .permitAll());

                http.csrf(csrf -> csrf.disable());

                http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
                http.addFilterAfter(onboardingFilter, JWTFilter.class);

                return http.build();
        }

        @Bean
        public BCryptPasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }
}
