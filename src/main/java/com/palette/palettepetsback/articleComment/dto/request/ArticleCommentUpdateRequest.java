package com.palette.palettepetsback.articleComment.dto.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class ArticleCommentUpdateRequest {

    private Long articleCommentId;
    private String content;



}
