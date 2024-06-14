package com.palette.palettepetsback.Article.articleView.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageableDTO {

    private int page;
    private String where;
    private String boardName;
    private String sort;
    private Boolean dir;

}
