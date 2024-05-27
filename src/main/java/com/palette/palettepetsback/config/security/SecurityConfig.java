package com.palette.palettepetsback.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.palette.palettepetsback.config.jwt.CustomLogoutFilter;
import com.palette.palettepetsback.config.jwt.JWTFilter;
import com.palette.palettepetsback.config.jwt.JWTUtil;
import com.palette.palettepetsback.config.jwt.LoginFilter;
import com.palette.palettepetsback.config.security.handlers.CustomAccessDeniedHandler;
import com.palette.palettepetsback.config.security.handlers.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Collections;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JWTUtil jwtUtil;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final ObjectMapper objectMapper;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // 보안 및 로그인 방식 관리
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors((cors) -> cors.configurationSource(request -> {
                    CorsConfiguration configuration = new CorsConfiguration();
                    configuration.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));
                    configuration.setAllowedMethods(Collections.singletonList("*"));
                    configuration.setAllowedHeaders(Collections.singletonList("*"));
                    configuration.setAllowCredentials(true);
                    configuration.setMaxAge(3600L);
                    // 클라이언트에 인증 헤더를 보낼 때 허용 필수
                    configuration.setExposedHeaders(Collections.singletonList("Authorization"));

                    return configuration;
                    }))
//                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);
        // 경로 별 인가
        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/login", "/logout", "/", "/members/**").permitAll()
                        .anyRequest().permitAll()
//                        .anyRequest().authenticated()
                );
        // jwt 관련 필터들 적용 - 로그인 / username&password 인증 / 로그아웃 필터
        http
                .addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class)
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, objectMapper),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new CustomLogoutFilter(jwtUtil), LogoutFilter.class);
        // 세션 매니저 설정 - STATELESS (JWT 사용을 위한 무상태 설정)
        http
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        // 시큐리티 에러 핸들링 (401 , 403) todo 커스텀 401, 403 에러 핸들러 작성
        http
                .exceptionHandling(ex ->
                        ex
                                .authenticationEntryPoint(authenticationEntryPoint())
                                .accessDeniedHandler(accessDeniedHandler())
                );

        return http.build();
    }

    // password encoder : Bcrypt 타입 사용
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 인증 매니저 -> 로그인 필터 사용
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // 403 Exception handlers
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    // 401 Exception handlers
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new CustomAuthenticationEntryPoint();
    }
}