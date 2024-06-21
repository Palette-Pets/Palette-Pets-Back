package com.palette.palettepetsback.Article.redis.repository;

import com.palette.palettepetsback.Article.redis.ArticleWriteRedis;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ArticleWriteRedisRepository extends CrudRepository<ArticleWriteRedis,String> {

    Optional<ArticleWriteRedis> findByMemberId(Long memberId);

    List<ArticleWriteRedis> findAllByMemberId(Long memberId);
}
