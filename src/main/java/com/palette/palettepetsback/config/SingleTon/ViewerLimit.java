package com.palette.palettepetsback.config.SingleTon;

import com.palette.palettepetsback.config.jwt.AuthInfoDto;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class ViewerLimit {

    private final RedisTemplate<String, String> redisTemplate;

    public boolean viewLimit(HttpServletRequest request) {

        String sessionId = request.getSession().getId();
        String key = "session_id_"+sessionId;

        if(!redisTemplate.hasKey(key)) {
            System.out.println("======="+sessionId+"조회수 상승================");
            redisTemplate.opsForValue().set(key, sessionId, 600, TimeUnit.SECONDS);
            return true;
        }
        else{
            System.out.println("=======이미 조회수를 올린 사람입니다.================");
            return false;
        }
    }
}
