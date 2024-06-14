package com.palette.palettepetsback.carrot.dto;

import com.palette.palettepetsback.member.entity.Member;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


@AllArgsConstructor
@Data
@ToString
@NoArgsConstructor
public class CarrotRequestDTO {
    private Long carrotId;
    private String carrotTitle;
    private String carrotContent;
    private Integer carrot_price;
    private String carrotImg;
    private Member memberId;
    private String carrotTag;

}
