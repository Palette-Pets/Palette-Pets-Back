package com.palette.palettepetsback.config.SingleTon;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.palette.palettepetsback.config.exceptions.BadWordException;
import io.jsonwebtoken.io.IOException;
import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import kr.co.shineware.nlp.komoran.model.Token;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BadWordService {
    private Set<String> bannedWords;

    public BadWordService() {
        try {
            // 클래스 경로 내의 "banned_words.json" 파일을 읽어옵니다.
            ClassPathResource resource = new ClassPathResource("banned_words.json");

            ObjectMapper mapper = new ObjectMapper();
            Map<String, List<String>> json = mapper.readValue(resource.getInputStream(), new TypeReference<>() {});

            // "banned_words" 키의 값을 Set<String>으로 변환하여 bannedWords 변수에 저장합니다.
            bannedWords = new HashSet<>(json.getOrDefault("banned_words", Collections.emptyList()));
        } catch (IOException | java.io.IOException e) {
            // 파일 읽기에 실패한 경우 bannedWords를 빈 Set으로 초기화합니다.
            bannedWords = Collections.emptySet();
        }
    }

    public Boolean containsBadWord(String text) {
        // text에 bannedWords에 포함된 단어가 포함되어 있는지 확인합니다.
        return bannedWords.stream().anyMatch(text::contains);
    }

    public void filtering(String text){
        if(containsBadWord(text)){
            System.out.println("=============금지어 발견==========================");
            throw new BadWordException(text+"는 금지어입니다. 수정해주세요");
        }
    }

    public void filterKomoran(String content){
        Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);
        KomoranResult analyzeResultList = komoran.analyze(content);
        List<Token> tokenList = analyzeResultList.getTokenList();
        for (Token token : tokenList) {

            if ("NNG".equals(token.getPos())) { // NNG 품사만 필터링
                System.out.println("token : "+ token.getMorph());
                filtering(token.getMorph());
            }
        }
    }
}
