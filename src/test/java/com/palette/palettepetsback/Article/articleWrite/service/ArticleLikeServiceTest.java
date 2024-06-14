//package com.palette.palettepetsback.Article.articleWrite.service;
//
//import com.palette.palettepetsback.Article.Article;
//import com.palette.palettepetsback.Article.ArticleLike;
//import com.palette.palettepetsback.Article.articleView.repository.ArticleRepository;
//import com.palette.palettepetsback.Article.articleWrite.repository.ArticleLikeRepository;
//import com.palette.palettepetsback.member.entity.Member;
//import com.palette.palettepetsback.member.repository.MemberRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class ArticleLikeServiceTest {
//    @Mock
//    private ArticleLikeRepository articleLikeRepository;
//
//    @Mock
//    private ArticleRepository articleRepository;
//
//    @Mock
//    private MemberRepository memberRepository;
//
//    @InjectMocks
//    private ArticleLikeService articleLikeService;
//
//    private Article article;
//    private Member member;
//    private ArticleLike articleLike;
//
////    @BeforeEach
////    void setUp() {
////        MockitoAnnotations.openMocks(this);
////
////        article = new Article();
////        article.setArticleId(1L);
////
////        member = new Member();
////        member.setMemberId(1L);
////
////        articleLike = ArticleLike.builder()
////                .article(article)
////                .member(member)
////                .createdAt(LocalDateTime.now())
////                .build();
////    }
//
//    @Test
//    void likeArticleTest() {
//        when(articleRepository.findById(1L)).thenReturn(Optional.of(article));
//        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
//        when(articleLikeRepository.findByArticleAndMember(article, member)).thenReturn(Optional.empty());
//
//        articleLikeService.likeArticle(1L, 1L);
//
//        verify(articleLikeRepository, times(1)).save(any(ArticleLike.class));
//    }
//
//    @Test
//    void unlikeArticleTest() {
//        when(articleRepository.findById(1L)).thenReturn(Optional.of(article));
//        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
//        when(articleLikeRepository.findByArticleAndMember(article, member)).thenReturn(Optional.of(articleLike));
//
//        articleLikeService.unlikeArticle(1L, 1L);
//
//        verify(articleLikeRepository, times(1)).delete(articleLike);
//    }
//
//    @Test
//    void getArticleLikesTest() {
//        when(articleRepository.findById(1L)).thenReturn(Optional.of(article));
//        when(articleLikeRepository.findByArticle(article)).thenReturn(List.of(articleLike));
//
//        List<ArticleLike> likes = articleLikeService.getArticleLikes(1L);
//
//        assertThat(likes).hasSameClassAs(1);
//    }
//
//    @Test
//    void getArticleLikeCountTest() {
//        when(articleRepository.findById(1L)).thenReturn(Optional.of(article));
//        when(articleLikeRepository.countByArticle(article)).thenReturn(1L);
//
//        long count = articleLikeService.getArticleLikeCount(1L);
//
//        assertThat(count).isEqualTo(1L);
//    }
//}