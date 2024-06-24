package com.palette.palettepetsback.articleComment.entity;

import com.palette.palettepetsback.Article.Article;

import com.palette.palettepetsback.config.jwt.AuthInfoDto;
import com.palette.palettepetsback.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="article_comment")
@Getter
public class ArticleComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "article_comment_id")
    private Long articleCommentId;

    @ManyToOne(fetch = FetchType.LAZY) // LAZY 로딩 지연로딩 하면좋음
    @JoinColumn(name = "article_id")
    private Article article;

//    @Column(name ="article_id")
//    private Long articleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_who", referencedColumnName = "member_id",nullable = false)
    @OnDelete(action= OnDeleteAction.CASCADE)
    private Member member;



    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "ref")
    private int ref; // 댓글들의 그룹번호 부모댓글과 자식댓글은 모두 똑같은 ref를 가진다

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private ArticleComment parentId;

    @Column(name = "content", nullable = false)
    private String content;
    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted = false;

    @Builder.Default
    @OneToMany(mappedBy = "parentId", orphanRemoval = true)
    private List<ArticleComment> children = new ArrayList<>();


    public void setContent(String content) {
        this.content = content;
    }

    @PrePersist
    public void prePersist(){
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.isDeleted=false;
    }
    @PostPersist
    public void postPersist() {
        if(this.ref == 0){
            this.ref = Math.toIntExact(this.articleCommentId);
        }
    }


    public boolean isOwnComment(Member member) {
        return this.member.equals(member);
    }
}
