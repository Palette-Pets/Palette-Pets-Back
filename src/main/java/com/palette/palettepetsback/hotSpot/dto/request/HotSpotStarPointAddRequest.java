package com.palette.palettepetsback.hotSpot.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class HotSpotStarPointAddRequest {

    @NotNull
    private Long hotSpotId;
    @Min(1)
    @Max(5)
    private Integer rating;
    private Long memberId;
}
