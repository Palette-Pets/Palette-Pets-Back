package com.palette.palettepetsback.Article.articleView.service;

import com.palette.palettepetsback.Article.Article;
import com.palette.palettepetsback.Article.QArticle;
import com.palette.palettepetsback.Article.articleView.DTO.response.ArticleResponseDTO;
import com.palette.palettepetsback.Article.articleView.DTO.PageableDTO;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import kr.co.shineware.nlp.komoran.core.Komoran;
import kr.co.shineware.nlp.komoran.model.KomoranResult;
import kr.co.shineware.nlp.komoran.model.Token;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleKomoranService {
    private final JPAQueryFactory jpaQueryFactory;
    private final Integer PAGE_SIZE;

    @Transactional(readOnly = true)
    public List<ArticleResponseDTO> searchLabelList(PageableDTO pd) {
        QArticle article = QArticle.article;
        int offset = (pd.getPage()-1) * PAGE_SIZE;
        int limit = offset + PAGE_SIZE;

        //사용자로 부터 문장을 입력 받아 Komoran 분리기로 명사/동서/조사 별로 분리
        Komoran komoran = new Komoran(DEFAULT_MODEL.FULL);
        KomoranResult analyzeResultList = komoran.analyze(pd.getWhere());
        List<Token> tokenList = analyzeResultList.getTokenList(); //분리된 단어를 Token 리스트에 넣기
        ArrayList<String> searchList = new ArrayList<>(); //주어진 Token 문장의 명사만 분리
        for(Token token: tokenList) {
            if ("NNG".equals(token.getPos()) && token.getMorph().length() > 1) { //명사 형태라면 수행해줘
                searchList.add(token.getMorph());
            }
        }
        BooleanBuilder where = new BooleanBuilder(); //위에서 분리 된 명사를 검색 조건으로 넣음
        for(String search: searchList) {
            where.or(article.articleTags.like("%"+search+"%"));
        }

        //정렬 조건i
        PathBuilder<?> entityPath = new PathBuilder<>(Article.class, "article");
        Order order = pd.getDir() ? Order.DESC : Order.ASC;
        List<OrderSpecifier> orderSpecifiers = new ArrayList<>();
        orderSpecifiers.add(new OrderSpecifier(order, entityPath.get(pd.getSort())));

        //검색과 정렬 조건으로 SQL 조회
        List<Article> articles = jpaQueryFactory
                .selectFrom(article)
                .where(where)
                .orderBy(orderSpecifiers.toArray(new OrderSpecifier[orderSpecifiers.size()]))
                .offset(offset).limit(limit)
                .fetch();

        //ArticleResponseDTO articleResponseDTO = new ArticleResponseDTO();
        //articles의 결과 값을 articleResponseDTO에 담아서 보내기

        List<ArticleResponseDTO> articleResponseDTOList = articles.stream()
                .map(responseDTO -> new ArticleResponseDTO(responseDTO))
                .collect(Collectors.toList());

        //System.out.println(articleResponseDTOList);
        //결과 리턴
        return articleResponseDTOList;

    }
}
