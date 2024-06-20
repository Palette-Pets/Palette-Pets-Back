package com.palette.palettepetsback.Article.articleWrite.dto.response;

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
public class ArticleWriteResponseDto {

    private Long ArticleId;
    private String created_who;
    private String memberImage;
    private String title;
    private String content;
    private String articleTags;
    private Integer countLoves;
    private Integer countReport;
    private Integer countReview;
    private List<ArticleImageDto>images;
    private LocalDateTime createdAt;

    public static ArticleWriteResponseDto toDto (Article article, String created_who,String memberImage){
        return new ArticleWriteResponseDto(
                article.getArticleId(),
                created_who,
                memberImage,
                article.getTitle(),
                article.getContent(),
                article.getArticleTags(),
                article.getCountLoves(),
                article.getCountReport(),
                article.getCountReview(),
                article.getImages().stream().map(ArticleImageDto::toDto).collect(toList()),
                article.getCreatedAt()

        );
    }
}
