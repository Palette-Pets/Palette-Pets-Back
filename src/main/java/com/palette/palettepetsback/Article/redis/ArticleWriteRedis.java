package com.palette.palettepetsback.Article.redis;

import org.springframework.data.annotation.Id;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@Data
@RedisHash(value = "ArticleWriteRedis",timeToLive = 60)
@Builder
@ToString
public class ArticleWriteRedis {
    @Id
    String writeId;

    @Indexed
    Long memberId;

    String expirationTime;

}







