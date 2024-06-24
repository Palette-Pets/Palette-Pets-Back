package com.palette.palettepetsback.Article.articleView.DTO.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.palette.palettepetsback.Article.Article;
import com.palette.palettepetsback.Article.articleWrite.dto.request.ArticleImageDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.toList;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleResponseDTO {

    private Long articleId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
    private Long createdWho;
    private String content;
    private String title;
    private String articleTags;
    private String articleHead;
    private String boardName;
    private Integer countLoves;
    private Integer countViews;
    private Integer countReview;
    private List<ArticleImageDto> images;

    public ArticleResponseDTO(Article responseDTO) {

        this.articleId = responseDTO.getArticleId();
        this.createdAt = responseDTO.getCreatedAt();
        this.createdWho = responseDTO.getCreatedWho();
        this.content = responseDTO.getContent();
        this.title = responseDTO.getTitle();
        this.articleTags = responseDTO.getArticleTags();
        this.articleHead = responseDTO.getArticleHead();
        this.boardName = String.valueOf(responseDTO.getBoardName());
        this.countLoves = responseDTO.getCountLoves();
        this.countViews = responseDTO.getCountViews();
        this.countReview = responseDTO.getCountReview();
        this.images = responseDTO.getImages().stream().map(ArticleImageDto::toDto).collect(toList());
    }



}
