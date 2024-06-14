package com.palette.palettepetsback.member.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberImgRequest {
    @NotBlank
    private String imgUrl; // S3에 저장한 주소값
    @NotNull
    private Long memberId; //아이디
}
