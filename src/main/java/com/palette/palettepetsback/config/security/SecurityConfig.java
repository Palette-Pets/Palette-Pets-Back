package com.palette.palettepetsback.config.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.palette.palettepetsback.config.jwt.filter.CustomLogoutFilter;
import com.palette.palettepetsback.config.jwt.filter.JWTFilter;
import com.palette.palettepetsback.config.jwt.JWTUtil;
import com.palette.palettepetsback.config.jwt.filter.LoginFilter;
import com.palette.palettepetsback.config.jwt.redis.RefreshTokenRepository;
import com.palette.palettepetsback.config.oauth2.FailureHandler;
import com.palette.palettepetsback.config.oauth2.CustomSuccessHandler;
import com.palette.palettepetsback.config.security.handlers.CustomAccessDeniedHandler;
import com.palette.palettepetsback.config.security.handlers.CustomAuthenticationEntryPoint;
import com.palette.palettepetsback.member.dto.Role;
import com.palette.palettepetsback.member.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;
    private final FailureHandler failureHandler;
    private final RefreshTokenRepository refreshTokenRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        // 보안 및 로그인 방식 관리
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors((cors) -> cors.configurationSource(request -> {
                    CorsConfiguration configuration = new CorsConfiguration();
                    configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
                    configuration.setAllowedMethods(Collections.singletonList("*"));
                    configuration.setAllowedHeaders(Collections.singletonList("*"));
                    configuration.setAllowCredentials(true);
                    configuration.setMaxAge(3600L);
                    // 클라이언트에 인증 헤더를 보낼 때 허용 필수
                    configuration.setExposedHeaders(Collections.singletonList("Authorization"));

                    return configuration;
                    }))
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable);
        // 경로 별 인가
        http
                .authorizeHttpRequests((auth) -> auth
                                .requestMatchers(HttpMethod.POST, "/login").permitAll()
                                .requestMatchers(HttpMethod.POST, "/memberF/**").permitAll()
                                .requestMatchers(HttpMethod.POST, "/member/**").hasAnyRole(Role.USER.name(), Role.ADMIN.name())
                                .requestMatchers(HttpMethod.POST, "/feed/**").hasAnyRole(Role.USER.name(), Role.ADMIN.name())
                                .requestMatchers(HttpMethod.POST, "/join").permitAll()
                                .requestMatchers(HttpMethod.POST, "/reissue").permitAll()
                                .requestMatchers("/logout", "/").permitAll()
                                .anyRequest().permitAll()
//                        .anyRequest().authenticated()
                );
        // jwt 관련 필터들 적용 - 로그인 / username&password 인증 / 로그아웃 필터
        http
                .addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class)
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration),
                                jwtUtil, objectMapper, refreshTokenRepository),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new CustomLogoutFilter(jwtUtil, refreshTokenRepository), LogoutFilter.class);
//        // 세션 매니저 설정 - STATELESS (JWT 사용을 위한 무상태 설정)
        http
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

//        // 시큐리티 에러 핸들링 (401 , 403) todo 커스텀 401, 403 에러 핸들러 작성
        http
                .exceptionHandling(ex ->
                        ex
                                .authenticationEntryPoint(authenticationEntryPoint())
                                .accessDeniedHandler(accessDeniedHandler())
                );
        http
                .oauth2Login((oauth2) -> oauth2
                    .userInfoEndpoint((userInfoEndpointConfig) ->
                        userInfoEndpointConfig
                            .userService(customOAuth2UserService))
                        .successHandler(customSuccessHandler)
                        .failureHandler(failureHandler)
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