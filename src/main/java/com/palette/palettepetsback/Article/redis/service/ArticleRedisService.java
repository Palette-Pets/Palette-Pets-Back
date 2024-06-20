package com.palette.palettepetsback.Article.redis.service;

import com.palette.palettepetsback.Article.redis.ArticleWriteRedis;
import com.palette.palettepetsback.Article.redis.ReportArticleRedis;
import com.palette.palettepetsback.Article.redis.repository.ArticleWriteRedisRepository;
import com.palette.palettepetsback.Article.redis.repository.ArticleReportRedisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class ArticleRedisService {

    private final ArticleWriteRedisRepository articleWriteRedisRepository;
    private final ArticleReportRedisRepository articleReportRedisRepository;

    /**
     * 글 도배 방지 정보를 저장.
     * @param articleWriteRedis 저장할 정보
     * @return 저장된 정보
     */
    public ArticleWriteRedis saveArticleWrite(ArticleWriteRedis articleWriteRedis){
        return articleWriteRedisRepository.save(articleWriteRedis);
    }

    /**
     * 글 도배 방지 정보 조회
     * @param writeId 조회할 ID
     * @return 조회된 정보
     */
    public Optional<ArticleWriteRedis> findArticleWriteById(String writeId){
        return articleWriteRedisRepository.findByWriteId(writeId);
    }


    /**
     *  글 신고 정보를 저장
     * @param reportArticleRedis 저장할 정보
     * @return 저장된 정보
     */
    public ReportArticleRedis saveReportArticle(ReportArticleRedis reportArticleRedis){
        return articleReportRedisRepository.save(reportArticleRedis);
    }

    /**
     * 글 신고 정보 조회
     * @param reportId 조회할 ID
     * @return 조회된 정보
     */
    public Optional<ReportArticleRedis> findReportArticleById(String reportId){
        return articleReportRedisRepository.findByReportId(reportId);
    }
}
