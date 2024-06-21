package com.palette.palettepetsback.hotSpot.entity;

import com.palette.palettepetsback.hotSpot.dto.request.HotSpotUpdateRequest;
import com.palette.palettepetsback.hotSpot.dto.response.HotSpotListResponse;
import com.palette.palettepetsback.member.entity.Member;
import lombok.*;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name = "hot_spot") // 테이블명
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class HotSpot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hot_spot_id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id") // 작성자 -> 회원 (ADMIN만 쓰게 하기)
    private Member member;
    @Column(name = "created_at") // 생성일
    private LocalDateTime createdAt;
    @Column(name = "modified_at") // 수정일
    private LocalDateTime modifiedAt;
    @Column(name = "place_name") // 장소 명
    private String placeName;
    @Column(name = "simple_content") // 내용 미리보기
    private String simpleContent;
    @Column(name = "content") // 내용
    private String content;
    @Column(name = "address", length = 500) // 주소
    private String address;
    @Column(name = "lat") //위도
    private Double lat;
    @Column(name = "lng") //경도
    private Double lng;
    @Column(name = "count_views") // 조회수
    private Integer countViews;
    @Column(name = "is_deleted") // 삭제
    private Boolean isDeleted;
    //imgHotSpot 양방향 연결
    @OneToMany(mappedBy = "hotSpot", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<ImgHotSpot> imgHotSpots = new ArrayList<>();

    // 명소 추천 글 등록 메서드
    @Builder
    public HotSpot(Member member, String placeName, String simpleContent, String content, String address, Double lat, Double lng) {
        this.member = member;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
        this.placeName = placeName;
        this.simpleContent = simpleContent;
        this.content = content;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.countViews = 0;
        this.isDeleted = false;
    }

    // countViews : 조회수 플러스 메서드
    public void plusCountViews() {
        this.countViews++;
    }

    //update 메서드
    public void updateHotSpot(HotSpotUpdateRequest dto) {
        this.modifiedAt = LocalDateTime.now();
        this.placeName = dto.getPlaceName();
        this.simpleContent =dto.getSimpleContent();
        this.content = dto.getContent();
        this.address = dto.getAddress();
        this.lat = dto.getLat();
        this.lng = dto.getLng();
    }

    //delete 메서드
    public void changeIsDeleted() {
        this.isDeleted = !this.isDeleted;
    }

    //Entity to dto
    public HotSpotListResponse toDto(Integer rating, String imgUrl) {

        return HotSpotListResponse.builder()
                .hotSpotId(this.id)
                .createAt(createdAt)
                .modifiedAt(modifiedAt)
                .placeName(placeName)
                .simpleContent(simpleContent)
                .address(address)
                .countViews(countViews)
                .rating(rating)
                .imgUrl(imgUrl)
                .build();

    }
}
