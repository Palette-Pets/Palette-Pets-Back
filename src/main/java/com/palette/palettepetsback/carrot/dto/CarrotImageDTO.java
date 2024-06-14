package com.palette.palettepetsback.carrot.dto;

import com.palette.palettepetsback.carrot.domain.Carrot;
import jakarta.persistence.Column;
import lombok.*;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CarrotImageDTO {
    private Carrot carrotId;
    private String carrotImageUrl;
}
