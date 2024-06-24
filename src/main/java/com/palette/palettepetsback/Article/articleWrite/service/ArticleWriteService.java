package com.palette.palettepetsback.Article.articleWrite.service;

import com.palette.palettepetsback.Article.Article;
import com.palette.palettepetsback.Article.ArticleImage;
import com.palette.palettepetsback.Article.ArticleLike;
import com.palette.palettepetsback.Article.articleWrite.dto.request.*;

import com.palette.palettepetsback.Article.articleWrite.dto.response.ArticleUpdateResponseDto;
import com.palette.palettepetsback.Article.articleWrite.dto.response.ArticleWriteResponseDto;
import com.palette.palettepetsback.Article.articleWrite.repository.*;
import com.palette.palettepetsback.Article.exception.type.ArticleNotFoundException;
import com.palette.palettepetsback.Article.exception.type.MemberNotEqualsException;
import com.palette.palettepetsback.Article.redis.ArticleWriteRedis;
import com.palette.palettepetsback.Article.redis.LikeArticleRedis;
import com.palette.palettepetsback.Article.redis.ReportArticleRedis;
import com.palette.palettepetsback.Article.redis.repository.ArticleWriteRedisRepository;
import com.palette.palettepetsback.config.SingleTon.Singleton;
import com.palette.palettepetsback.config.Storage.NCPObjectStorageService;
import com.palette.palettepetsback.config.jwt.AuthInfoDto;
import com.palette.palettepetsback.config.jwt.JWTUtil;
import com.palette.palettepetsback.member.entity.Member;
import com.palette.palettepetsback.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ArticleWriteService {

    private final ArticleWriteRepository articleWriteRepository;
    private final ArticleLikeRepository articleLikeRepository;
    private final NCPObjectStorageService objectStorageService;
    private final FileService fileService;
    private final MemberRepository memberRepository;
    private final ImgArticleRepository imgArticleRepository;



    //게시글 이미지 Object Storage 등록
    public String uploadArticleImage(String filePath, MultipartFile file){
        return objectStorageService.uploadFile(Singleton.S3_BUCKET_NAME,filePath,file);

    }

    //게시글 이미지 정보 DB 등록
    @Transactional
    public  Long  createImgArticle(ArticleImageDto dto) {

        Article article = articleWriteRepository.findById(dto.getArticleId()).orElseThrow(()->new IllegalArgumentException("article not found"));

        ArticleImage saved = imgArticleRepository.save(
                ArticleImage.builder()
                        .imgUrl(dto.getImgUrl())
                        .article(article)
                        .build()
        );
        return saved.getId();
    }

    //get
    public List<Article> index() {
        return articleWriteRepository.findAll();
    }

    // 글 등록
    @Transactional
    public Article create(ArticleWriteDto dto) {

        StringJoiner joiner = new StringJoiner(",");
        for (String item : dto.getArticleTags()) {
            joiner.add(item);
        }

        Article articleWrite = Article.builder()
                .createdWho(dto.getCreatedWho())
                .boardName(Article.ComminityBoard.valueOf(dto.getBoardName()))
                .articleHead(dto.getArticleHead())
                .articleTags(joiner.toString())
                .title(dto.getTitle())
                .content(dto.getContent())
                .build();
        if(articleWrite.getArticleId()!=null){
            return null;
        }

        return articleWriteRepository.save(articleWrite);
    }

    //DELETE
    @Transactional
    public void delete(Long id) {
//        // 1. 대상 찾기
        Article target = articleWriteRepository.findById(id)
                .orElseThrow(()->new IllegalArgumentException("article not found"));
        // 2. 잘못된 요청 처리하기
        if(target == null){
            throw new ArticleNotFoundException();
        }
        //3. 대상 삭제하기 대신  상태변경하기
        target.markAsDeleted();
    }

    //게시글 단건 조회
    @Transactional(readOnly = true)
    public ArticleWriteResponseDto findArticle(Long articleId) {
        Article article = articleWriteRepository.findById(articleId)
                .orElseThrow(ArticleNotFoundException::new);

        Member member = article.getMember();

        return ArticleWriteResponseDto.toDto(article,member.getMemberNickname(),member.getMemberImage());
    }

    //업데이트용 게시글 단건 조회
    @Transactional(readOnly = true)
    public ArticleUpdateResponseDto getUpdateArticle(Long articleId) {
        Article article = articleWriteRepository.findById(articleId)
                .orElseThrow(ArticleNotFoundException::new);

        return ArticleUpdateResponseDto.toDto(article);
    }



    //업데이트 이미 업로드한 사진이 있고 등록하는 사진이 있을 때
    @Transactional
    public ArticleWriteResponseDto editArticle(Long articleId, ArticleUpdateRequest req, AuthInfoDto authInfoDto,List<MultipartFile> files) {

        Article article = articleWriteRepository.findById(articleId)
                .orElseThrow(ArticleNotFoundException::new);

        validateArticleOwner(authInfoDto,article);

        Member member = memberRepository.findById(authInfoDto.getMemberId()).orElseThrow(()->new IllegalArgumentException("멤버를 찾을수없습니다"));

        //1. 기본적으로 업데이트 하는 Entity 메소드

        article.commonUpdate(req);

        if(article.getImages() == null && files != null) {
            //2. 기존 이미지가 없고 현재 이미지가 들어올때
            // Object Storage에 추가 , 테이블에 레코드 추가
            List<ArticleImage> addList = article.convertImageFilesToImages(fileService.upload(files));
            article.addImages(addList);
        }
        else if(article.getImages() != null && files == null) {
            //3. 기존 이미지가 있고 현재 이미지는 없을때
            // object storage 삭제, 테이블 레코드 삭제
            List<Long> deletedImageIds = article.getImages().stream()
                    .map(ArticleImage::getId)
                    .toList();
            fileService.delete(article.getImages());
            List<ArticleImage> delList = article.convertImageIdsToImages(deletedImageIds);
            article.deleteImages(delList);
        }
        else if(article.getImages() !=null && files !=null) {
            //4. 기존 이미지가 있고 현재 이미지가 변경되었을때
            // 기존 이미지 삭제, 테이블에서 삭제 -> Object Storage에 추가 , 테이블에 추가

            // 기존 이미지 url
            List<String> imgUrls = article.getImages().stream()
                    .map(ArticleImage::getImgUrl)
                    .toList();
            // 기존 이미지와 클라이언트의 이미지를 비교하여 추가할 이미지를 선택
            List<MultipartFile> addImages = files.stream()
                    .filter(file -> !imgUrls.contains(file.getOriginalFilename()))
                    .toList();
            // files 이미지 name
            List<String> originalFileNames = files.stream()
                    .map(MultipartFile::getOriginalFilename)
                    .toList();
            // 기존 이미지와 클라이언트의 이미지를 비교하여 삭제할 이미지를 선택
            List<Long> deletedImages = article.getImages().stream()
                    .filter(articleImage -> !originalFileNames.contains(articleImage.getImgUrl()))
                    .map(ArticleImage::getId)
                    .toList();

            List<String> uploadedImageUrls = new ArrayList<>();
            Article.ImageUpdateResult result = null;
            // 1. 추가 할 이미지를 업로드
            try {
                for (MultipartFile file : addImages) {
                    String fileName = objectStorageService.uploadFile(Singleton.S3_BUCKET_NAME, "article/img", file);
                    uploadedImageUrls.add(fileName);
                }
                req.setAddImages(uploadedImageUrls);
                req.setDeletedImages(deletedImages);

                //2. 데이터 베이스 등록 및 삭제
                result = article.update(req);

            } catch (Exception e) {
                //3. 실패시 추가 한 이미지 롤백
                uploadedImageUrls.forEach(imageKey -> objectStorageService.deleteFile(Singleton.S3_BUCKET_NAME, "article/img/" + imageKey));
                throw e;
            }

            // 4. 테이터베이스에서 삭제 한 이미지를 삭제
            result.getDeletedImages().forEach(deletedImage -> objectStorageService.deleteFile(Singleton.S3_BUCKET_NAME, "article/img/" + deletedImage.getImgUrl()));
        }

        return ArticleWriteResponseDto.toDto(article,member.getMemberNickname(),member.getMemberImage());
    }

    private void validateArticleOwner(AuthInfoDto authInfoDto, Article article) {
        if(!authInfoDto.getMemberId().equals(article.getMember().getMemberId())){
            throw new MemberNotEqualsException();
        }
    }

    //게시글 조회시 조회수 up
    @Transactional
    public void updateCountViews(Long articleId) {
        Article article = articleWriteRepository.findById(articleId)
                .orElseThrow(ArticleNotFoundException::new);
        articleWriteRepository.updateCountViews(article.getArticleId(), article.getCountViews()+1);
    }
    //댓글 등록시 댓글 개수 up
    @Transactional
    public void updateCountReviews(Long articleId) {
        Article article = articleWriteRepository.findById(articleId)
                .orElseThrow(ArticleNotFoundException::new);
        log.info("articleId:{}", article.getArticleId());
        articleWriteRepository.updateCountReviews(article.getArticleId(), article.getCountReview()+1);
    }


    // 신고 RDBMS countReport+1
    @Transactional
    public void incrementReportCount(Long articleId) {
        Article article = articleWriteRepository.findById(articleId)
                .orElseThrow(ArticleNotFoundException::new);
        log.info("articleId:{}", article.getArticleId());
        articleWriteRepository.incrementReportCount(article.getArticleId(),article.getCountReport()+1);
    }


}



