package com.palette.palettepetsback.carrot.dto;

import com.palette.palettepetsback.carrot.domain.Carrot;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@AllArgsConstructor
@Data
@ToString
@NoArgsConstructor
public class CarrotResponseDTO {
    private Long carrotId;
    private String carrotTitle;
    private String carrotContent;
    private Integer carrot_price;
    private LocalDateTime carrot_createdAt;
    private Long memberId;
    private String carrotTag;
    private Integer carrotLike;
    private Integer carrotView;

    public CarrotResponseDTO(Carrot responseDTO) {
        this.carrotId = responseDTO.getCarrotId();
        this.carrotTitle = responseDTO.getCarrotTitle();
        this.carrotContent = responseDTO.getCarrotContent();
        this.carrot_price = responseDTO.getCarrot_price();
        this.carrot_createdAt = responseDTO.getCarrot_createdAt();
        this.memberId = responseDTO.getMember().getMemberId();
        this.carrotTag = responseDTO.getCarrotTag();
        this.carrotLike = responseDTO.getCarrotLike();
        this.carrotView = responseDTO.getCarrotView();
    }
}
