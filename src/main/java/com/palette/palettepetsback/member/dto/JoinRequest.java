package com.palette.palettepetsback.member.dto;

import com.palette.palettepetsback.member.entity.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class JoinRequest { //유저 이탈을 막기위해 최소한의 정보만 입력받게 함. 나머지 정보는 언제 기입할지 고민중

    @Pattern(regexp="^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])+[.][a-zA-Z]{2,3}$", message="이메일 형식이 맞지 않습니다.")
    @NotBlank(message = "이메일을 입력해 주세요.")
    private String email;
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*[0-9])(?=.*[$@!%*#?&]).{8,16}$",
            message = "비밀번호는 영문자, 숫자, 특수문자를 포함한 8~16자리여야 합니다.")
    @NotBlank(message = "비밀번호를 입력해 주세요.")
    private String password;
    @NotBlank(message = "비밀번호 확인을 입력해 주세요.")
    private String checkPassword;
    @NotBlank(message = "닉네임을 입력해 주세요.")
    @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9-_]{2,10}$", message = "닉네임은 특수문자를 제외한 2~10자리여야 합니다.")
    private String nickName;

    // 비밀번호 암호화
    public Member toEntity(String encodedPassword) {
        return Member.builder()
                .email(this.email)
                .password(encodedPassword)
                .memberNickname(this.nickName)
                .role(Role.USER)
                .loginType("basic")
                .build();
    }

}
