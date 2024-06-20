package com.palette.palettepetsback.Article.articleWrite.service;

import com.palette.palettepetsback.Article.Article;
import com.palette.palettepetsback.Article.ArticleLike;
import com.palette.palettepetsback.Article.ArticleLikeId;
import com.palette.palettepetsback.Article.articleView.repository.ArticleRepository;
import com.palette.palettepetsback.Article.articleWrite.dto.request.ArticleLikeRequestDto;
import com.palette.palettepetsback.Article.articleWrite.dto.response.ArticleLikeResponseDto;
import com.palette.palettepetsback.Article.articleWrite.repository.ArticleLikeRepository;
import com.palette.palettepetsback.Article.articleWrite.repository.ArticleWriteRepository;
import com.palette.palettepetsback.Article.redis.LikeArticleRedis;
import com.palette.palettepetsback.member.entity.Member;
import com.palette.palettepetsback.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ArticleLikeService {
    private final ArticleLikeRepository articleLikeRepository;
    private final ArticleRepository articleRepository;
    private final ArticleWriteRepository articleWriteRepository;
    private final MemberRepository memberRepository;



    @Transactional
    public String likeArticle(Long articleId,Long memberId){
        Article article = articleRepository.findById(articleId)
                .orElseThrow(()->new IllegalArgumentException("게시글 찾을수없음"));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()->new IllegalArgumentException("Member not found"));


        Optional<ArticleLike>existingLike= articleLikeRepository.findByArticleAndMember(article,member);
        if(existingLike.isPresent()){
            return "이미 좋아요를 눌렀습니다.";
        }

        articleWriteRepository.incrementLoveCount(articleId,article.getCountLoves()+1);

        ArticleLikeId articleLikeId = new ArticleLikeId(articleId, memberId);

        ArticleLike articleLike = ArticleLike.builder()
                .id(articleLikeId)
                .article(article)
                .member(member)
                .createdAt(LocalDateTime.now())
                .build();

        articleLikeRepository.save(articleLike);
        return "좋아요가 등록되었습니다.";
    }

    public void unlikeArticle(Long articleId,Long memberId){
        Article article =articleRepository.findById(articleId)
                .orElseThrow(()->new IllegalArgumentException("Article not found"));
        Member member =memberRepository.findById(memberId)
                .orElseThrow(()->new IllegalArgumentException("Member not found"));

        Optional<ArticleLike>existingLike = articleLikeRepository.findByArticleAndMember(article,member);
        existingLike.ifPresent(articleLikeRepository::delete);
    }

    public List<ArticleLike>getArticleLikes(Long articleId){
        Article article = articleRepository.findById(articleId)
                .orElseThrow(()->new IllegalArgumentException("Article not found"));
        return articleLikeRepository.findByArticle(article);
    }

    public long getArticleLikeCount(Long articleId){
        Article article = articleRepository.findById(articleId)
                .orElseThrow(()->new IllegalArgumentException("Article not found"));
        return articleLikeRepository.countByArticle(article);
    }

}