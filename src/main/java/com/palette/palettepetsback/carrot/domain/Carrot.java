package com.palette.palettepetsback.carrot.domain;

import com.palette.palettepetsback.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Builder
@Data
@Entity
@Table(name = "carrot")
@NoArgsConstructor
public class Carrot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "carrot_id")
    private Long carrotId;

    @Column(name = "carrot_title")
    private String carrotTitle;

    @Column(columnDefinition = "TEXT", name = "carrot_content")
    private String carrotContent;

    @Column(name = "carrot_price")
    private Integer carrot_price;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "carrot_created_at")
    private LocalDateTime carrot_createdAt;

    @Column(name = "carrot_tag")
    private String carrotTag;

    @Column(name = "carrot_state", columnDefinition = "TINYINT")
    private int carrotState;

    @Column(name = "carrot_like")
    private Integer carrotLike;

    @Column(name = "carrot_view")
    private Integer carrotView;

    //저장되기 전 실행 메서드(default 값 지정)
    @PrePersist
    public void prePersist() {
        this.carrot_createdAt = LocalDateTime.now();
        this.carrotLike = 0;
        this.carrotView = 0;
    }

    //수정 되기 전 실행 메서드
    @PreUpdate
    public void preUpdate() {
        this.carrot_createdAt = LocalDateTime.now();
    }

    //CarrotImage와 매핑(orphanRemoval : 영속성 전이 설정)
//    @OneToMany(mappedBy = "Carrot", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<CarrotImage> carrotImages = new ArrayList<>();
}
