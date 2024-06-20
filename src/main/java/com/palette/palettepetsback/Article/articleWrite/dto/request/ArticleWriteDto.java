package com.palette.palettepetsback.Article.articleWrite.dto.request;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ArticleWriteDto {

//    private Long articleId;
    private String boardName;
    private String articleHead;
    private List<String> articleTags;
    @Size(min =2,max = 20)
    private String title;
    @Size(min=10,max=1000)
    private String content;
    private Long createdWho;
}
