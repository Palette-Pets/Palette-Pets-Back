package com.palette.palettepetsback.carrot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarrotRecentDTO {
    private Long carrotId;
    private String carrotTitle;
    private String carrotImage;
    private int carrotLike;
    private int carrotView;
}
