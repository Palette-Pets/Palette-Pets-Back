package com.palette.palettepetsback.Article.articleWrite.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArticleUpdateUploadDto {

    private Long articleId;
    private String imgUrl;

}
