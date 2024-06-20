package com.palette.palettepetsback.hotSpot.entity;

import com.palette.palettepetsback.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "hot_spot_star_point") // 테이블명
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HotSpotStarPoint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hot_spot_star_point_id")
    private Long id;
    @Column(name = "rating") // 별점
    private Integer rating;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hot_spot_id") // 평가 대상 게시물
    private HotSpot hotSpot;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id") // 별점 평가자
    private Member member;

    @Builder
    public HotSpotStarPoint(HotSpot hotSpot, Member member, Integer rating) {
        this.hotSpot = hotSpot;
        this.member = member;
        this.rating = rating;
    }
}
