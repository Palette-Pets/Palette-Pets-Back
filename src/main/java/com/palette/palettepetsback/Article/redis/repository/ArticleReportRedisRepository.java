package com.palette.palettepetsback.Article.redis.repository;

import com.palette.palettepetsback.Article.redis.ReportArticleRedis;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ArticleReportRedisRepository extends CrudRepository<ReportArticleRedis,String> {
    Optional<ReportArticleRedis>findByReportId(String reportId);
}
