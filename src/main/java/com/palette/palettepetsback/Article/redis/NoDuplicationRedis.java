package com.palette.palettepetsback.Article.redis;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.redis.core.RedisHash;

@Data
@RedisHash(value = "ArticleLike", timeToLive = (24 * 60 * 60))
@Builder
@ToString
public class NoDuplicationRedis {


}
