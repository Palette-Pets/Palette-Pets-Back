package com.palette.palettepetsback.Article.articleWrite.repository;


import com.palette.palettepetsback.Article.Article;
import com.palette.palettepetsback.Article.ArticleLike;

import com.palette.palettepetsback.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface ArticleLikeRepository extends JpaRepository<ArticleLike, Long> {
    Optional<ArticleLike> findByArticleAndMember(Article article, Member member);
    List<ArticleLike> findByArticle(Article article);
    Long countByArticle(Article article);

    Boolean existsByArticleAndMember(Article article, Member member);
}