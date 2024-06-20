package com.palette.palettepetsback.Article.redis;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Data
@RedisHash(value = "ArticleLikeRedis", timeToLive = (24 * 60 * 60))
@Builder
@ToString
public class LikeArticleRedis {

    @Id
     String likeId;

    @Indexed
    Long memberId;

    Long articleId;

}

// 글 도배 방지 Redis 1분 (60초)
// 글 좋아요 24시간
// 글 신고 24시간