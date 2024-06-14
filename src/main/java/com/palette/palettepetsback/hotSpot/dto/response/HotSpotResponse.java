package com.palette.palettepetsback.hotSpot.dto.response;

import com.palette.palettepetsback.hotSpot.entity.ImgHotSpot;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class HotSpotResponse {
    private Long hotSpotId; // 장소 아이디
    private String memberNickname; //유저 닉네임
    private LocalDateTime createAt;
    private LocalDateTime modifiedAt;
    private String placeName;
    private String simpleContent;
    private String content;
    private String address;
    private Double lat;
    private Double lng;
    private Integer countViews;
    private Integer rating;
    // img_hot_spot
    private List<ImgHotSpotResponse> imgList;
}
