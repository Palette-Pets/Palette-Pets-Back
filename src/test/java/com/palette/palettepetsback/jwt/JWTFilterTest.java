//package com.palette.palettepetsback.jwt;
//
//import com.palette.palettepetsback.config.jwt.filter.JWTFilter;
//import com.palette.palettepetsback.config.jwt.JWTUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.transaction.annotation.Transactional;
//
//import static org.mockito.Mockito.when;
//
//@Slf4j
//@Transactional
//@ExtendWith(MockitoExtension.class)
//public class JWTFilterTest {
//
//    private MockMvc mockMvc;
//
//    @MockBean
//    private JWTUtil jwtUtil;
//
//    @MockBean
//    private JWTFilter jwtFilter;
//
//    // 테스트할 토큰
//    private String validToken;
//    private String expiredToken;
//    private String invalidToken;
//
//    @BeforeEach
//    void setUp() throws Exception {
//        validToken = "valid_token";
//        expiredToken = "expired_token";
//        invalidToken = "invalid_token";
//
//        when(jwtUtil.isExpired(validToken)).thenReturn(false);
//        when(jwtUtil.isExpired(expiredToken)).thenReturn(true);
//        when(jwtUtil.getCategory(validToken)).thenReturn("access");
//        when(jwtUtil.getCategory(invalidToken)).thenReturn("refresh");
//    }
//
//    @DisplayName("유효_access_token_성공사례")
//    @Test
//    void doFilter_validToken() throws Exception {
//
//    }
//
//    @DisplayName("expired_token_만료_실패사례_만료임")
//    @Test
//    void doFilterInternal_ExpiredToken_Unauthorized() throws Exception {
//
//    }
//
//    @DisplayName("invalid_token_인증_실패사례_access가아님")
//    @Test
//    void doFilterInternal_InvalidToken_Unauthorized() throws Exception {
//
//    }
//}
