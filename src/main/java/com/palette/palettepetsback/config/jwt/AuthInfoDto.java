package com.palette.palettepetsback.config.jwt;

import com.palette.palettepetsback.member.dto.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthInfoDto {

    private Long memberId;
    private String email;
    private Role role;
    private String memberNickname;
}
