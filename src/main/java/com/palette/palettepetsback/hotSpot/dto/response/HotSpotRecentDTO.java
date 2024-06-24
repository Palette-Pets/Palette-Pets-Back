package com.palette.palettepetsback.hotSpot.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HotSpotRecentDTO {
    public Long hotSpotId;
    public Long userId;
//    public String placeImg;
    public String placeName;
    public Object uploadAt;
}
