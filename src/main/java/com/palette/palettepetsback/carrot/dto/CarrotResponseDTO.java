package com.palette.palettepetsback.carrot.dto;

import com.palette.palettepetsback.carrot.domain.Carrot;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
@Data
@ToString
@NoArgsConstructor
public class CarrotResponseDTO {
    private Long carrotId;
    private String carrotTitle;
    private String carrotContent;
    private Integer carrot_price;
    private LocalDateTime carrot_createdAt;
    private String memberId;
    private String carrotTag;
    private Integer carrotLike;
    private Integer carrotView;
    private String img;


    public CarrotResponseDTO(Carrot responseDTO) {
        this.carrotId = responseDTO.getCarrotId();
        this.carrotTitle = responseDTO.getCarrotTitle();
        this.carrotContent = responseDTO.getCarrotContent();
        this.carrot_price = responseDTO.getCarrot_price();
        this.carrot_createdAt = responseDTO.getCarrot_createdAt();
        this.memberId = responseDTO.getMember().getMemberName();
        this.carrotTag = responseDTO.getCarrotTag();
        this.carrotLike = responseDTO.getCarrotLike();
        this.carrotView = responseDTO.getCarrotView();
    }

}
