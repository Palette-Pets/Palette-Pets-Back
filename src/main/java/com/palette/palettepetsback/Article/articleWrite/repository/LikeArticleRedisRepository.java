package com.palette.palettepetsback.Article.articleWrite.repository;

import com.palette.palettepetsback.Article.redis.LikeArticleRedis;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface LikeArticleRedisRepository extends CrudRepository<LikeArticleRedis, Long>{
    Optional<List<LikeArticleRedis>> findAllByMemberId(Long memberId);
}
