package com.palette.palettepetsback.Article.articleWrite.service;

import com.palette.palettepetsback.Article.Article;
import com.palette.palettepetsback.Article.ArticleLike;
import com.palette.palettepetsback.Article.articleView.repository.ArticleRepository;
import com.palette.palettepetsback.Article.articleWrite.dto.request.ArticleLikeRequestDto;
import com.palette.palettepetsback.Article.articleWrite.dto.response.ArticleLikeResponseDto;
import com.palette.palettepetsback.Article.articleWrite.repository.ArticleLikeRepository;
import com.palette.palettepetsback.Article.articleWrite.repository.LikeArticleRedisRepository;
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
    private final MemberRepository memberRepository;

    // Redis
    private final LikeArticleRedisRepository likeArticleRedisRepository;

    public void likeArticle(Long articleId,Long memberId){
        Article article = articleRepository.findById(articleId)
                .orElseThrow(()->new IllegalArgumentException("게시글 찾을수없음"));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(()->new IllegalArgumentException("Member not found"));

        Optional<ArticleLike>existingLike= articleLikeRepository.findByArticleAndMember(article,member);
        if(existingLike.isPresent()){
            return;
        }

        ArticleLike articleLike = ArticleLike.builder()
                .article(article)
                .member(member)
                .createdAt(LocalDateTime.now())
                .build();

        articleLikeRepository.save(articleLike);
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




    //좋아요 Redis 조회
    public Boolean isLike(Long articleId, Long memberId) {

        Optional<List<LikeArticleRedis>> likeArticleRedis = likeArticleRedisRepository.findAllByMemberId(memberId);

        if(likeArticleRedis.isPresent()){
            log.info("likeArticleRedis:{}", likeArticleRedis.get());
            for(LikeArticleRedis like : likeArticleRedis.get()){
                if(like.getArticleId().equals(articleId)){
                    log.info("like:{}", like.getArticleId());
                    log.info("articleId:{}", articleId);
                    return true;
                }
            }
        }
        return false;
    }

    //좋아요 Redis 저장
    public Boolean resistLike(Long articleId, Long memberId) {

        try{
            likeArticleRedisRepository.save(LikeArticleRedis.builder()
                    .likeId(UUID.randomUUID().toString())
                    .memberId(memberId)
                    .articleId(articleId)
                    .build());
            return true;
        }
        catch (Exception e){
            log.info("좋아요 - Redis 저장 실패");
            return false;
        }
    }

}