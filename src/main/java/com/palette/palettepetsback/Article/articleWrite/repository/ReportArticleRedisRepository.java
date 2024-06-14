package com.palette.palettepetsback.Article.articleWrite.repository;


import com.palette.palettepetsback.Article.redis.ReportArticleRedis;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReportArticleRedisRepository extends CrudRepository<ReportArticleRedis, Long> {

    Optional<List<ReportArticleRedis>> findAllByMemberId(Long memberId);
}
