//package com.palette.palettepetsback.Article.articleWrite.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.palette.palettepetsback.Article.articleWrite.dto.request.ArticleLikeRequestDto;
//import com.palette.palettepetsback.Article.articleWrite.service.ArticleLikeService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.util.Collections;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(LikeController.class) // WebMvc 테스트에 필요한 설정만 로드합니다.
//public class LikeControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc; // Spring MVC의 동작을 모방하기 위해 사용합니다.
//
//    @MockBean
//    private ArticleLikeService articleLikeService; // 실제 서비스 대신 mock 객체를 주입받습니다.
//
//    @Autowired
//    private ObjectMapper objectMapper; // 객체를 JSON으로 변환하기 위해 사용합니다.
//
//    private ArticleLikeRequestDto articleLikeRequestDto;
//
//    @BeforeEach
//    void setUp() {
//        // 테스트를 위한 초기 데이터 설정
//        articleLikeRequestDto = new ArticleLikeRequestDto(1L, 1L);
//    }
//
//    @Test
//    void testLikeArticle() throws Exception {
//        // 게시글 좋아요 테스트
//        mockMvc.perform(post("/like")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(articleLikeRequestDto)))
//                .andExpect(status().isCreated()); // 생성됨(201) 상태 코드 예상
//    }
//
//    @Test
//    void testUnlikeArticle() throws Exception {
//        // 게시글 좋아요 취소 테스트
//        mockMvc.perform(delete("/like/{articleId}/{memberId}", 1L, 1L))
//                .andExpect(status().isNoContent()); // 내용 없음(204) 상태 코드 예상
//    }
//
//    @Test
//    void testGetArticleLikes() throws Exception {
//        Mockito.when(articleLikeService.getArticleLikes(1L)).thenReturn(Collections.emptyList());
//
//        mockMvc.perform(get("/like/{articleId}", 1L))
//                .andExpect(status().isOk()) // 정상(200) 상태 코드 예상
//                .andExpect(jsonPath("$").isArray()); // JSON 배열 반환 예상
//    }
//
//    @Test
//    void testGetArticleLikeCount() throws Exception {
//        Mockito.when(articleLikeService.getArticleLikeCount(1L)).thenReturn(0L);
//
//        mockMvc.perform(get("/like/count/{articleId}", 1L))
//                .andExpect(status().isOk()) // 정상(200) 상태 코드 예상
//                .andExpect(content().string("0")); // "0" 문자열 반환 예상
//    }
//}
