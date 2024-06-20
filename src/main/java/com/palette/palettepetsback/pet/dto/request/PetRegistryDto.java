package com.palette.palettepetsback.pet.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetRegistryDto {
    /**
     * Pet 등록 요청 Dto
     */
    private Long createdWho; // 나중에 member_id 연관관계 맺기
    @NotBlank(message = "이름을 입력해주세요.")
//    @Pattern(regexp = "^[a-zA-Z가-힣\\s]+$", message = "이름은 한글, 영문, 공백만 허용됩니다.")
    private String petName;
    private String petImage;
    @NotBlank(message = "대분류를 입력해주세요.")
    @Pattern(regexp = "^[a-zA-Z가-힣\\s]+$", message = "대분류는 한글, 영문, 공백만 허용됩니다.")
    private String petCategory1;
    @NotBlank(message = "소분류를 입력해주세요.")
    @Pattern(regexp = "^[a-zA-Z가-힣\\s]+$", message = "소분류는 한글, 영문, 공백만 허용됩니다.")
    private String petCategory2;
    @NotBlank
    private String petBirth;
    @NotBlank
    private String petGender;
    private Integer petWeight;
}
