package com.palette.palettepetsback.member.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MyPageRespons {
    private String nickname;
    private String img;
    private int follower;
    private int following;
}
