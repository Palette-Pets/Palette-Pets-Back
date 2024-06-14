package com.palette.palettepetsback.hotSpot.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class HotSpotListResponse {
    private Long hotSpotId; // 장소 아이디
    private LocalDateTime createAt;
    private LocalDateTime modifiedAt;
    private String placeName;
    private String simpleContent;
    private String address;
    private Integer countViews;
    private String imgUrl;
    private Integer rating;
}
