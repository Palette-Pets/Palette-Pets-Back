package com.palette.palettepetsback.Article.redis.controller;

import com.palette.palettepetsback.Article.redis.ArticleWriteRedis;
import com.palette.palettepetsback.Article.redis.ReportArticleRedis;
import com.palette.palettepetsback.Article.redis.service.ArticleRedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class ArticleRedisController {

    private final ArticleRedisService articleRedisService;

    /**
     * 글 도배 방지 정보를 저장
     * @param articleWriteRedis 저장할 정보
     * @return 저장된 정보
     */
    @PostMapping("/ArticleRedis")
    public ArticleWriteRedis saveArticleWrite(@RequestBody ArticleWriteRedis articleWriteRedis){
        return articleRedisService.saveArticleWrite(articleWriteRedis);
    }

    /**
     * 글 도배 방지 정보 조회
     * @param writeId 조회할 ID
     * @return 조회된 정보
     */

    @GetMapping("ArticleRedis/{id}")
    public Optional<ArticleWriteRedis>findArticleWriteById(@PathVariable String writeId){
        return articleRedisService.findArticleWriteById(writeId);
    }


    /**
     * 글 신고 정보를 저장
     * @param reportArticleRedis 저장할 정보
     * @return 저장된 정보
     */
    @PostMapping("/report")
    public ReportArticleRedis saveReportArticle(@RequestBody ReportArticleRedis reportArticleRedis){
        return articleRedisService.saveReportArticle(reportArticleRedis);
    }

    /**
     * 글 신고 정보 조회
     * @param id 조회할 ID
     * @return 조회된 정보
     */

    @GetMapping("/report/{id}")
    public Optional<ReportArticleRedis> findReportArticleById(@PathVariable String id){
        return articleRedisService.findReportArticleById(id);
    }
}
