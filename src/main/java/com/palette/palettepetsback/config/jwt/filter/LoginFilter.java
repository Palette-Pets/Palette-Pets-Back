package com.palette.palettepetsback.config.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.palette.palettepetsback.config.exceptions.exception.BasicLoginIOException;
import com.palette.palettepetsback.config.jwt.JWTUtil;
import com.palette.palettepetsback.config.jwt.redis.RefreshTokenRepository;
import com.palette.palettepetsback.config.jwt.redis.entity.RefreshToken;
import com.palette.palettepetsback.config.security.CustomUserDetails;
import com.palette.palettepetsback.member.dto.LoginRequest;
import com.palette.palettepetsback.member.entity.Member;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final ObjectMapper objectMapper;
    // todo 추후 redis로 리프레시 토큰 저장소 사용 추가 필요
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        // POST 요청의 /login 경로만 통과하도록.
        if (!request.getMethod().equals("POST") && !request.getRequestURI().matches("^\\/login$")) {

            log.info("로그인 요청은 /login 경로에 POST 메서드 여야 합니다");
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        } else {

            LoginRequest loginRequest;

            // JSON 으로 요청 : request -> username, password 추출
            try {

                ObjectMapper objectMapper = new ObjectMapper();
                ServletInputStream inputStream = request.getInputStream();
                String loginBody = StreamUtils.copyToString(inputStream, StandardCharsets.UTF_8);
                loginRequest = objectMapper.readValue(loginBody, com.palette.palettepetsback.member.dto.LoginRequest.class);

                String username = loginRequest.getUsername();
                String password = loginRequest.getPassword();

                // 인증 토큰 생성
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(username, password);
                // 검증 정보 -> 검증 매니저에게 전달
                return authenticationManager.authenticate(authenticationToken);

            } catch (IOException e) {
                log.info("[LoginFilter] - 검증 중 IO 에러 발생, ", e);
                throw new BasicLoginIOException(e.getMessage());
            }
        }
    }

    // 성공
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {

        // todo 회원 정보 테스트 필요
        // userDetails -> member, role 추출
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Member member = userDetails.getMember();
        // role
        String role = authentication.getAuthorities().iterator().next().getAuthority();

        Map<String, Object> claims = new HashMap<>();
        claims.put("memberId", member.getMemberId());
        claims.put("email", member.getEmail());
        claims.put("role", member.getRole().name());
        claims.put("memberNickname", member.getMemberNickname());

        // token 발급
//        String access = jwtUtil.generateToken("access", claims, 10 * 1000L); // 어세스 토큰 - 테스트용 10초 만료
        String access = jwtUtil.generateToken("access", claims, 60 * 60 * 1000L); // 어세스 토큰 - 1시간 만료 (12-24시간)
//        String refresh = jwtUtil.generateToken("refresh", claims, 60L); // 리프레시 토큰 - 테스트용 바로 만료
        String refresh = jwtUtil.generateToken("refresh", claims, 24 * 60 * 60 * 1000L); // 리프레시 토큰 - 24시간 만료 (1일~한달)

        // todo RTR 사용시 -> 레디스 리프레시 토큰 저장소에 발급한 리프레시 토큰 저장
        RefreshToken refreshToken = RefreshToken.builder()
                .refreshToken(refresh)
                .email(member.getEmail())
                .expiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000).getTime())
                .build();
        RefreshToken saved = refreshTokenRepository.save(refreshToken);
        log.info("refresh token 저장소 저장 = {}", saved);
        if (saved.getRefreshToken() == null) {
            throw new RuntimeException("redis 리프레시 토큰 저장소 저장 실패");
        }

        // response 설정
        // access 토큰 -> Authorization 헤더에 넣어서 반환
        response.setHeader("Authorization", "Bearer " + access);
        // refresh 토큰 : HttpOnly 쿠키에 넣어서 반환
        response.addCookie(createCookie("refresh", refresh));
        response.setStatus(HttpServletResponse.SC_OK);
    }

    // 실패
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        
        // 로그인 실패 -> 401 인증 실패 에러 반환
        log.info("login fail!!");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(objectMapper.writeValueAsString(false));
    }

    // HttpOnly 쿠키 생성 메서드
    private Cookie createCookie(String key, String token) {
        Cookie cookie = new Cookie(key, token);
        cookie.setMaxAge(24 * 60 * 60); // refresh 와 같은 24시간
        cookie.setHttpOnly(true);
//        cookie.setSecure(true); // http ssl 적용 시
//        cookie.setPath("/"); // 쿠키 적용 경로 커스텀 지정 가능

        return cookie;
    }

    // JSON 요청 로그인 정보 DTO
//    @Getter
//    @Setter
//    static class LoginRequest {
//        private String username;
//        private String password;
//    }

    // 로그인 성공 JSON 반환값
    @Getter
    @Setter
    @Builder
    static class MemberResponseDto {
        private Long memberId;
        private String email;
        private String role;
    }
}
