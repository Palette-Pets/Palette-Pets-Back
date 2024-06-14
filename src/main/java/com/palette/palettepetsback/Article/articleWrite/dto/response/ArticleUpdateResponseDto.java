package com.palette.palettepetsback.Article.articleWrite.dto.response;

import com.palette.palettepetsback.Article.Article;
import com.palette.palettepetsback.Article.articleWrite.dto.request.ArticleImageDto;
import lombok.*;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ArticleUpdateResponseDto {

    private String boardName;
    private String articleTags;
    private String articleHead;
    private String title;

    private String content;
    private List<ArticleImageDto> images;

    public static ArticleUpdateResponseDto toDto(Article article){
        return ArticleUpdateResponseDto.builder()
                .boardName(String.valueOf(article.getBoardName()))
                .articleTags(article.getArticleTags())
                .articleHead(article.getArticleHead())
                .title(article.getTitle())
                .content(article.getContent())
                .images(article.getImages().stream().map(ArticleImageDto::toDto).collect(toList()))
                .build();

    }

}
