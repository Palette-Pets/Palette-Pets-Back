package com.palette.palettepetsback.Article;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.palette.palettepetsback.Article.articleWrite.dto.request.ArticleUpdateRequest;
import com.palette.palettepetsback.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

import static java.util.stream.Collectors.toList;
import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

@Entity
@Data
@Builder
@Table(name = "article")
@NoArgsConstructor
@AllArgsConstructor
public class Article {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="article_id")
    private Long articleId;

    @Column(name="created_who")
    private Long createdWho;

    @Column(name="created_at")
    private LocalDateTime createdAt;

    @Column(columnDefinition = "TEXT", name = "content", nullable = false)
    private String content;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "article_tags")
    private String articleTags;
    @Column(name = "state")
    @Enumerated(EnumType.STRING)
    private Article.State state;

    @Column(name = "count_loves")
    private Integer countLoves;

    @Column(name = "count_report")
    private Integer countReport;

    @Column(name = "count_views")
    private Integer countViews;

    @Column(name = "count_review")
    private Integer countReview;

    @Column(name="is_deleted", nullable = false)
    private boolean isDeleted;

    //추가 Entity
    @Column(name="board_name")
    @Enumerated(EnumType.STRING)
    private ComminityBoard boardName;

    @Column(name="article_head")
    private String articleHead;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("memberId")
    @JoinColumn(name = "created_who", referencedColumnName = "member_id",nullable = false)
    @OnDelete(action= OnDeleteAction.CASCADE)
    private Member member;

    @OneToMany(mappedBy = "article",cascade=CascadeType.PERSIST,orphanRemoval = true)
    private List<ArticleImage> images;

    @PrePersist //Entity 실행 전 수행하는 마라미터로 default 값을 지정O
    public void prePersist(){
        this.createdAt = LocalDateTime.now();
        this.countLoves = 0;
        this.countReport = 0;
        this.countReview = 0;
        this.countViews = 0;
        this.isDeleted = false;
        this.state = State.ACTIVE;
    }

    public void markAsDeleted(){
        this.state = State.DELETED;
        this.isDeleted=true;
        this.createdAt=LocalDateTime.now();
    }

    @PreUpdate //사용하므로 수정할때마다 현재시간으로 시간이 저장
    protected void onUpdate(){
        if(this.state != State.DELETED) {
            createdAt = LocalDateTime.now();
            state = State.MODIFIED;
        }
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setState(String modified) {
        this.state= State.valueOf(modified);
    }



    // 1. req에 addImages -> 등록해야 할 파일
    // 2. req에 deletedImages -> 삭제해야 할 파일
    public ImageUpdateResult update(ArticleUpdateRequest req) {

        StringJoiner joiner = new StringJoiner(",");
        for (String item : req.getArticleTags()) {
            joiner.add(item);
        }

        this.articleHead = req.getArticleHead();
        this.boardName = ComminityBoard.valueOf(req.getBoardName());
        this.articleTags = joiner.toString();
        this.title = req.getTitle();
        this.content = req.getContent();

        ImageUpdateResult result = findImageUpdateResult(req.getAddImages(),req.getDeletedImages());

        // 등록해야할 파일 Entity를 전해 주면 자동으로 JPA에서 추가
        addImages(result.getAddedImage());
        // 삭제해야할 파일 Entity를 전해 주면 자동으로 JPA에서 삭제
        deleteImages(result.getDeletedImages());

        return result;
    }



    private void addImages(List<ArticleImage> addedImages) {
        addedImages.forEach(addedImage->{
            images.add(addedImage);
            addedImage.initArticle(this);
        });
    }
    private void deleteImages(List<ArticleImage> deletedImages) {
        deletedImages.forEach(deletedImage->this.images.remove(deletedImage));
    }


    private ImageUpdateResult findImageUpdateResult(List<String> addedImageUrls, List<Long> deletedImageIds) {

        List<ArticleImage> addedImages = convertImageFilesToImages(addedImageUrls);
        List<ArticleImage> deletedImages = convertImageIdsToImages(deletedImageIds);
        return new ImageUpdateResult(addedImages,deletedImages);
    }

    private List<ArticleImage> convertImageIdsToImages(List<Long> imageIds) {
        return imageIds.stream()
                .map(this::convertImageIdToImage)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(toList());
    }

    private Optional<ArticleImage> convertImageIdToImage( Long id) {
     return this.images.stream()
             .filter(articleImage -> articleImage.isSameImageId(id))
             .findAny();
    }

    private List<ArticleImage> convertImageFilesToImages(List<String> imageFiles) {
        return imageFiles.stream()
                .map(ArticleImage::from)
                .collect(toList());
    }


    public void setBoardName(String modified) {
        this.boardName= ComminityBoard.valueOf(modified);
    }

    public enum ComminityBoard{
        FREEBOARD,INFORMATION,SHOW,QNA
    }

    public enum State{
        ACTIVE,MODIFIED,DELETED
    }

    //Patch
    public void patch(Article article) {
        if(article.title !=null)
            this.title = article.title;
        if(article.content != null)
            this.content = article.content;
        if(article.articleTags != null)
            this.articleTags = article.articleTags;
    }

    @Getter
    @AllArgsConstructor
    @ToString
    public static class ImageUpdateResult {

        private List<ArticleImage> addedImage;
        private List<ArticleImage> deletedImages;

    }
}
