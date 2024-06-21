package com.palette.palettepetsback.config.jwt;

import com.palette.palettepetsback.config.exceptions.exception.NotAuthenticatedException;
import com.palette.palettepetsback.config.security.CustomUserDetails;
import com.palette.palettepetsback.member.entity.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * JWT - 생성 및 관리 클래스
 * 구성
 *  - JWT 생성 메서드
 *  - JWT claims get 메서드
 *  - JWT 검증 메서드 : isExpired()
 *
 *  JWT 기본 스펙 (2024.05.27)
 *  - memberId : pk값
 *  - email : 회원 아이디 겸 이메일
 *  - role : 회원 권한
 *  - category : 어세스 토큰인지 리프레시 토큰인지 구분
 *  - 발행일
 *  - 만료기한
 *
 *  - (추후 필요하면 추가할 것들)
 *      hasPet : 반려동물을 등록하고 있는 지 여부 -> 반려동물과 관련된 기능에 접근 권한, 반려동물 등록 알람 여부 등등...
 */
@Slf4j
@Component
public class JWTUtil {

    private SecretKey secretKey;

    public JWTUtil(@Value("${jwt.secret}") String secret) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    /**
     * JWT 토큰 생성 메서드
     * @param claims
     * @param expiredMs
     * @return
     */
    public String generateToken(String category, Map<String, Object> claims, Long expiredMs) {

        claims.put("category", category);

        return Jwts.builder()
                .claims(claims)
                .issuedAt(new Date(System.currentTimeMillis())) // 현재 발행시간
                .expiration(new Date(System.currentTimeMillis() + expiredMs)) // 만료 시간
                .signWith(secretKey)
                .compact();
    }
    public String createJwt(String username, String role, Long expiredMs) {

        return Jwts.builder()
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    public static AuthInfoDto getMemberInfo() {

        // principal 정보 가져오기
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // jwt 인증을 거친 요청인지 검증
        if (principal.equals("anonymousUser")) {
            log.info("현재 익명 인증 객체 = {}", principal);
            throw new NotAuthenticatedException("스프링 시큐리티 세션에서 가져올 정보가 존재하지 않음");
        }
        // userDetails 캐스팅 수행하여 member 정보 획득
        CustomUserDetails userDetails = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Member member = userDetails.getMember();

        return AuthInfoDto.builder()
                .memberId(member.getMemberId())
                .email(member.getEmail())
                .role(member.getRole())
                .memberNickname(member.getMemberNickname())
                .build();
    }

    /**
     * JWT 검증 메서드 -> JWT 만료일 검증
     * @param token
     * @return
     */
    public Boolean isExpired(String token) {

        boolean isExpired = false;

        try {
            isExpired = Jwts
                    .parser()
                    .verifyWith(secretKey) // 서버가 가진 시크릿 키로 검증 -> 서버에서 발급한 JWT이 맞는가 검증
                    .build()
                    .parseSignedClaims(token) // claims 검증
                    .getPayload()
                    .getExpiration()
                    .before(new Date()); // 현재 날짜 기준 만료일 이전인지 검증
        } catch (ExpiredJwtException e) {
            log.info("JWT 검증 - Error 발생 {}", e.getMessage());
            isExpired = true;
        }

        return isExpired;
    }

    /**
     * 필요한 정보들 Claims에서 가져오는 메서드
     * @param token
     * @return 회원 세션 정보들
     */
    public Claims getClaims(String token) {
        
        return Jwts
                .parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String getEmail(String token) {

        return Jwts
                .parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("email", String.class);
    }

    public String getRole(String token) {

        return Jwts
                .parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("role", String.class);
    }

    public String getMemberNickname(String token) {

        return Jwts
                .parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("memberNickname", String.class);
    }

    /**
     * category : 토큰이 access 인지, refresh 인지 판단
     * @param token
     * @return String.class
     */
    public String getCategory(String token) {

        return Jwts
                .parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get("category", String.class);
    }
}
