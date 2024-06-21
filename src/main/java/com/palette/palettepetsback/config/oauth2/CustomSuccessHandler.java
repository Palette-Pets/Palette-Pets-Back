package com.palette.palettepetsback.config.oauth2;

import com.palette.palettepetsback.config.jwt.JWTUtil;
import com.palette.palettepetsback.config.jwt.redis.RefreshTokenRepository;
import com.palette.palettepetsback.config.jwt.redis.entity.RefreshToken;
import com.palette.palettepetsback.member.dto.CustomOAuth2User;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        //OAuth2User
        CustomOAuth2User customUserDetails = (CustomOAuth2User) authentication.getPrincipal();
        String username = customUserDetails.getUsername();
        Long memberId = customUserDetails.getUserDTO().getMemberId();
        String email = customUserDetails.getUserDTO().getEmail();

        log.info("username && = {}", username);
        log.info("memberId && = {}", memberId);
        log.info("email && = {}", email);


        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        Map<String, Object> claims = new HashMap<>();
        claims.put("memberId", memberId);
        claims.put("email", email);
        claims.put("role", role);

        // token 발급
//        String access = jwtUtil.generateToken("access", claims, 10 * 1000L); // 어세스 토큰 - 테스트용 10초 만료
        String access = jwtUtil.generateToken("access", claims, 60 * 60 * 1000L); // 어세스 토큰 - 1시간 만료 (12-24시간)
//        String refresh = jwtUtil.generateToken("refresh", claims, 60L); // 리프레시 토큰 - 테스트용 바로 만료
        String refresh = jwtUtil.generateToken("refresh", claims, 24 * 60 * 60 * 1000L); // 리프레시 토큰 - 24시간 만료 (1일~한달)

        // todo RTR 사용시 -> 레디스 리프레시 토큰 저장소에 발급한 리프레시 토큰 저장
        RefreshToken refreshToken = RefreshToken.builder()
                .refreshToken(refresh)
                .email(email)
                .expiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000).getTime())
                .build();
        RefreshToken saved = refreshTokenRepository.save(refreshToken);
        log.info("refresh token 저장소 저장 = {}", saved);
        if (saved.getRefreshToken() == null) {
            throw new RuntimeException("redis 리프레시 토큰 저장소 저장 실패");
        }

        // response 설정
        // access 토큰 -> Authorization 임시로 쿠키에 넣어서 반환 -> 프론트에서 바로 받아서 token 에 넣어주기
        response.addCookie(createCookie("Authorization", access, false));
        // refresh 토큰 : HttpOnly 쿠키에 넣어서 반환
        response.addCookie(createCookie("refresh", refresh, true));
        response.setStatus(HttpServletResponse.SC_OK);

//        String token = jwtUtil.createJwt(username, role, 60 * 60 * 60L);
//        response.addCookie(createCookie("Authorization", token));

        response.sendRedirect("http://localhost:3000?oauthCallback=true");
    }

    private Cookie createCookie(String key, String value, boolean isHttpOnly) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60 * 60 * 60);
        cookie.setPath("/");
        cookie.setHttpOnly(isHttpOnly);

        return cookie;
    }
}
