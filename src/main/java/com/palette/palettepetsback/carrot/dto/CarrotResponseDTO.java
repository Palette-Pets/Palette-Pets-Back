package com.palette.palettepetsback.carrot.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.palette.palettepetsback.carrot.domain.Carrot;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@Builder
@Data
@ToString
@NoArgsConstructor
public class CarrotResponseDTO {
    private Long carrotId;
    private String carrotTitle;
    private String carrotContent;
    private Integer carrotPrice;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime carrotCreatedAt;
    private Long memberId;
    private String memberNickname;
    private String carrotTag;
    private int carrotState;
    private Integer carrotLike;
    private Integer carrotView;
    private String carrotImg;
    private List<String> imgList;

    public CarrotResponseDTO(Carrot responseDTO) {
        this.carrotId = responseDTO.getCarrotId();
        this.carrotTitle = responseDTO.getCarrotTitle();
        this.carrotContent = responseDTO.getCarrotContent();
        this.carrotPrice = responseDTO.getCarrot_price();
        this.carrotCreatedAt = responseDTO.getCarrot_createdAt();
        this.memberId =responseDTO.getMember().getMemberId();
        this.memberNickname = responseDTO.getMember().getMemberName();
        this.carrotTag = responseDTO.getCarrotTag();
        this.carrotLike = responseDTO.getCarrotLike();
        this.carrotView = responseDTO.getCarrotView();
        this.carrotImg = responseDTO.getCarrotImage();
    }

}
