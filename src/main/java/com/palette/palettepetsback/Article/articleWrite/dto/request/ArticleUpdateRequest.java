package com.palette.palettepetsback.Article.articleWrite.dto.request;


import com.palette.palettepetsback.Article.ArticleImage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ArticleUpdateRequest {



    private String boardName;
    private String articleHead;
    private List<String> articleTags;

    @NotBlank(message="게시글 제목을 입력해주세요.")
    private String title;
    @NotBlank(message = "게시글 내용을 입력해주세요.")
    private String content;

    private Long createdWho;

    //들어가는 파일
    private List<String> addImages;

    //삭제되는 파일
    private List<Long> deletedImages;

}
