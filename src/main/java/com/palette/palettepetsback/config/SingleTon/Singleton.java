package com.palette.palettepetsback.config.SingleTon;

import com.palette.palettepetsback.config.aop.notification.TimeTrace;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Singleton {

    public static final String S3_BUCKET_NAME = "palettepets";

    @Bean
    public Integer PAGE_SIZE(){
        return 10;
    }

    @Bean
    public String BUCKET_NAME(){
        return "palettepets";
    }

//    @Bean
//    public TimeTrace timeTrace(){
//        return new TimeTrace();
//    }
}
