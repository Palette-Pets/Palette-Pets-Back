package com.palette.palettepetsback.Article.redis;


import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@Data
@RedisHash(value = "ArticleReport", timeToLive = (24 * 60 * 60))
@Builder
@ToString
public class ReportArticleRedis {

    @Id
    String reportId;
    @Indexed
    Long memberId;
    Long articleId;

}
