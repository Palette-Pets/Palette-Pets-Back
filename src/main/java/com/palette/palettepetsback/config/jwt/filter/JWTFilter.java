package com.palette.palettepetsback.config.jwt.filter;

import com.palette.palettepetsback.config.jwt.JWTUtil;
import com.palette.palettepetsback.config.security.CustomUserDetails;
import com.palette.palettepetsback.member.dto.Role;
import com.palette.palettepetsback.member.entity.Member;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 토큰과 함께 요청할 때 마다 -> JWT 검증 후, 인증 정보와 세션을 시큐리티 컨텍스트에 등록
 */
@Slf4j
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // request -> Authorization header 추출 및 access token 추출
        String authorization = request.getHeader("Authorization");

        // --- Authorization 토큰 헤더 검증
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            // 어세스 토큰이 없음 -> jwt 인증이 필요 없는 요청
            filterChain.doFilter(request, response);
            return;
        }

        // "Bearer " 떼기
        String token = authorization.split(" ")[1];
        log.info("JWT Token: {}", token);

        // 만료 시간 검증 -> 아니면 예외 발생
        if (jwtUtil.isExpired(token)) {
            response.getWriter().print("access token expired");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        // category 가 access 인지 확인 -> 아니면 refresh 이므로 예외 발생
        if (!jwtUtil.getCategory(token).equals("access")) {
            response.getWriter().print("invalid access token");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }
        // --- 검증 끝


        // 클레임에서 저장할 정보 가져오기
        Claims claims = jwtUtil.getClaims(token);
        Long memberId = claims.get("memberId", Long.class);
        String email = claims.get("email", String.class);
        String role = claims.get("role", String.class);
        String memberNickname = claims.get("memberNickname", String.class);

        // 회원 정보 넣기
        Member member = new Member(memberId, email, Role.valueOf(role), memberNickname);

        // 시큐리티 인증 토큰 생성
        CustomUserDetails userDetails = CustomUserDetails
                .builder()
                .member(member)
                .build();
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        // 시큐리티 컨텍스트에 인증 정보 등록
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//        log.info("security context getAuthentication : {}", SecurityContextHolder.getContext().getAuthentication());

        filterChain.doFilter(request, response);
    }
}
