package com.palette.palettepetsback.articleComment.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.palette.palettepetsback.articleComment.entity.ArticleComment;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@ToString
public class ArticleCommentListResponse {

    private Long articleCommentId;
    private String content;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createAt;
    //Member 닉네임, 프로필 사진
    private String memberNickname;
    private String memberImage;

    private List<ArticleCommentListResponse> children = new ArrayList<>();

    public ArticleCommentListResponse(Long articleCommentId, String content, LocalDateTime createAt, String memberNickname, String memberImage) {
        this.articleCommentId = articleCommentId;
        this.content = content;
        this.createAt = createAt;
        this.memberNickname = memberNickname;
        this.memberImage = memberImage;
    }

    public static ArticleCommentListResponse convertCommentToDto(ArticleComment comment) {
        return comment.isDeleted() ?
                new ArticleCommentListResponse(comment.getArticleCommentId(), "삭제된 댓글입니다.", null, null,null) :
                new ArticleCommentListResponse(comment.getArticleCommentId(),comment.getContent(), comment.getUpdatedAt(), comment.getMember().getMemberNickname(), comment.getMember().getMemberImage());
    }
}
