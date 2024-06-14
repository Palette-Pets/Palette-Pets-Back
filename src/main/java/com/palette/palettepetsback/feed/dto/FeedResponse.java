package com.palette.palettepetsback.feed.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
public class FeedResponse {
    private String text;
    private List<String> img;
    private Boolean writer=false;
}
