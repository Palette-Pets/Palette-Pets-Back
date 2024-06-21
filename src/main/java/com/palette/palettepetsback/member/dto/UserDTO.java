package com.palette.palettepetsback.member.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class UserDTO {

    private String role;
    private Long MemberId;
    private String email;
    private String name;
    private String username;

}
