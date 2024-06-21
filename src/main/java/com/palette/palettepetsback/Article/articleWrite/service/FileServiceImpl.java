package com.palette.palettepetsback.Article.articleWrite.service;


import com.palette.palettepetsback.Article.ArticleImage;
import com.palette.palettepetsback.config.SingleTon.Singleton;
import com.palette.palettepetsback.config.Storage.NCPObjectStorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FileServiceImpl implements FileService{

    private final NCPObjectStorageService objectStorageService;

    @Override
    public List<String> upload(List<MultipartFile> files) {

        List<String> uploadedImageUrls = new ArrayList<>();

        try{
            for(MultipartFile file : files) {
                String fileName = objectStorageService.uploadFile(Singleton.S3_BUCKET_NAME, "article/img", file);
                uploadedImageUrls.add(fileName);
            }
            return uploadedImageUrls;
        }catch (Exception e){
            log.error("Failed to upload file", e);
            uploadedImageUrls.forEach(imageKey -> objectStorageService.deleteFile(Singleton.S3_BUCKET_NAME, "article/img/" + imageKey));
            return null;
        }
    }

    @Override
    public void delete(List<ArticleImage> filename){
        try{
            for(ArticleImage image : filename) {
                objectStorageService.deleteFile(Singleton.S3_BUCKET_NAME, "article/img/" + image.getImgUrl());
            }
        }catch (Exception e){
            log.error("Failed to delete file", e);
        }

    }
}
