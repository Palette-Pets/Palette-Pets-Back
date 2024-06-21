package com.palette.palettepetsback.Article.articleWrite.service;


import com.palette.palettepetsback.Article.ArticleImage;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface FileService {
    List<String> upload(List<MultipartFile> file);

    void delete(List<ArticleImage> filename);
}
