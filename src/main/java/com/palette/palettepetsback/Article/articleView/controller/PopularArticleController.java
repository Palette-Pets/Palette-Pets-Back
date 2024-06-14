package com.palette.palettepetsback.Article.articleView.controller;

import com.palette.palettepetsback.Article.Article;
import com.palette.palettepetsback.Article.articleView.DTO.response.PopularArticleDTO;
import com.palette.palettepetsback.Article.articleView.service.PopularArticleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PopularArticleController {

    private final PopularArticleService articleService;

    @GetMapping("/popular")
    public ResponseEntity<List<PopularArticleDTO>> getPopularArticle(){
        return ResponseEntity.ok().body(articleService.likeArticle());
    }
}
