package com.palette.palettepetsback.member.entity;

import com.palette.palettepetsback.member.dto.Role;
import com.palette.palettepetsback.pet.entity.Pet;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Builder
@Table(name = "member")
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Member {

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    private List<Pet> pets = new ArrayList<>();

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;
    private String email;
    private String password;
    @Column(name = "member_name")
    private String memberName;
    @Column(name = "member_nickname")
    private String memberNickname;
    @Column(name = "member_address")
    private String memberAddress;
    @Column(name = "member_birth")
    private String memberBirth;
    @Column(name = "member_gender")
    private String memberGender;
    @Column(name = "member_phone")
    private String memberPhone;
    @Enumerated(EnumType.STRING)
    private Role role;
    @Column(name = "member_image")
    private String memberImage;
    @Column(name = "login_type")
    private String loginType;
    @Column(name = "is_deleted")
    private boolean isDeleted;

    @OneToMany(mappedBy = "followerId", cascade = CascadeType.ALL)
    private List<Follow> followerList = new ArrayList<>();

    @OneToMany(mappedBy = "followingId", cascade = CascadeType.ALL)
    private List<Follow> followingList = new ArrayList<>();

    @Builder
    public Member(String email, String password, String memberName,String memberNickname, String memberAddress,
                  String memberGender, String memberPhone,
                  String memberImage,
                  Role role ){
        this.email = email;
        this.password = password;
        this.memberName = memberName;
        this.memberNickname = memberNickname;
        this.memberAddress = memberAddress;
        this.memberGender = memberGender;
        this.memberPhone = memberPhone;
        this.memberImage = memberImage;
        this.role = role;
    }

    public Member(Long memberId, String email, Role role) {
        this.memberId = memberId;
        this.email = email;
        this.role = role;
    }

    public void existData(String email, String memberName, String memberNickname, String memberBirth, String memberGender, String memberPhone){
        this.email = email;
        this.memberName = memberName;
        this.memberNickname = memberNickname;
        this.memberBirth=memberBirth;
        this.memberGender=memberGender;
        this.memberPhone = memberPhone;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateNickname(String memberNickname) {
        this.memberNickname = memberNickname;
    }

    public void createNameAndPhoneAndAddress(String memberName, String memberPhone, String memberAddress) {
        this.memberName = memberName;
        this.memberPhone = memberPhone;
        this.memberAddress = memberAddress;
    }

    public void updateBirthGender(String memberBirth, String memberGender) {
        this.memberBirth=memberBirth;
        this.memberGender=memberGender;
    }
    public void saveProfile(String memberImage){
        this.memberImage = memberImage;
    }
}
