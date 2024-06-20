package com.palette.palettepetsback.hotSpot.dto.request;

// 필요한 라이브러리 임포트
import com.palette.palettepetsback.member.entity.Member;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Getter
@Setter
public class HotSpotAddRequest {

    private Long memberId;
    @NotBlank(message = "장소명을 반드시 기재해야 합니다")
    private String placeName;
    @NotBlank(message = "소개글을 반드시 기재해야 합니다")
    private String simpleContent;
    @NotBlank(message = "내용을 반드시 기재해야 합니다")
    private String content;
    @NotBlank(message = "주소 도로명을 반드시 기재해야 합니다")
    private String address;
    private Double lat;
    private Double lng;
}

