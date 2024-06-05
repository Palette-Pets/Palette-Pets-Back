package com.palette.palettepetsback.member.service;

import com.palette.palettepetsback.member.dto.*;
import com.palette.palettepetsback.member.entity.Member;
import com.palette.palettepetsback.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.palette.palettepetsback.member.entity.QMember.member;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        System.out.println(oAuth2User);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        OAuth2Response oAuth2Response = null;

        if (registrationId.equals("naver")) {

            oAuth2Response = new NaverResponse(oAuth2User.getAttributes());
        }
        else if (registrationId.equals("google")) {

            oAuth2Response = new GoogleResponse(oAuth2User.getAttributes());
        }
        else {

            return null;
        }




        //리소스 서버에서 발급 받은 정보로 사용자를 특정할 아이디값을 만듬
        String username = oAuth2Response.getProvider()+" "+oAuth2Response.getProviderId();
        Member existData = memberRepository.findByPassword(username);


        if (existData == null) { //oauth로 처음 로그인 한 사람

            String email = oAuth2Response.getEmail();
            Optional<Member> findEmail = memberRepository.findByEmail(email);
            
            //이메일 중복 확인
            if (findEmail.isPresent()) { //이메일이 이미 존재하는 경우
                System.out.println("findEmail = " + findEmail);
                OAuth2Error oAuth2Error = new OAuth2Error("error");
                throw new OAuth2AuthenticationException(oAuth2Error, oAuth2Error.toString());
            }

              Member member = Member.builder()
                      .password(username)
                      .email(oAuth2Response.getEmail())
                      .memberName(oAuth2Response.getName())
                      .memberNickname(oAuth2Response.getNickname())
                      .memberBirth(oAuth2Response.getBirthday())
                      .memberGender(oAuth2Response.getGender())
                      .memberPhone(oAuth2Response.getPhone_number())
                      .memberImage(oAuth2Response.getProfile_image())
                      .role(Role.USER)
                      .build();

            memberRepository.save(member);

            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(username);
            userDTO.setName(oAuth2Response.getName());
            userDTO.setRole("USER");

            return new CustomOAuth2User(userDTO);
        }
        else {
            //데이터 업데이트
            existData.existData(
                    oAuth2Response.getEmail(),
                    oAuth2Response.getName(),
                    oAuth2Response.getNickname(),
                    oAuth2Response.getBirthday(),
                    oAuth2Response.getGender(),
                    oAuth2Response.getPhone_number()
            );

            memberRepository.save(existData);

            UserDTO userDTO = new UserDTO();
            userDTO.setUsername(existData.getPassword());
            userDTO.setName(oAuth2Response.getName());

            return new CustomOAuth2User(userDTO);
        }
    }
}