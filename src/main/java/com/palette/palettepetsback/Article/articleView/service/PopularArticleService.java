package com.palette.palettepetsback.Article.articleView.service;

import com.palette.palettepetsback.Article.Article;
import com.palette.palettepetsback.Article.articleView.DTO.response.PopularArticleDTO;
import com.palette.palettepetsback.Article.articleView.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PopularArticleService {
    private final ArticleRepository articleRepository;

    public List<PopularArticleDTO> likeArticle(){
        return articleRepository.findPopularArticleByDate(LocalDateTime.now().minusDays(3));
    }
}
