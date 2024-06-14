package com.palette.palettepetsback.member.service;

import com.palette.palettepetsback.config.Mail.EmailResponseDTO;
import com.palette.palettepetsback.config.Mail.RegisterMail;
import com.palette.palettepetsback.config.SingleTon.Singleton;
import com.palette.palettepetsback.config.Storage.NCPObjectStorageService;
import com.palette.palettepetsback.config.exceptions.NoSuchPetException;
import com.palette.palettepetsback.member.dto.JoinRequest;
import com.palette.palettepetsback.member.dto.MemberImgRequest;
import com.palette.palettepetsback.member.dto.MemberRequest;
import com.palette.palettepetsback.member.entity.Member;
import com.palette.palettepetsback.member.repository.MemberRepository;
import com.palette.palettepetsback.pet.dto.request.ImgPetRegistryDto;
import com.palette.palettepetsback.pet.entity.ImgPet;
import com.palette.palettepetsback.pet.entity.Pet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final RegisterMail registerMail;
    private final NCPObjectStorageService objectStorageService;


    //이메일 중복확인
    public boolean checkEmailDuplicate(String email) {
        return memberRepository.existsByEmail(email);
    }
    //닉네임 중복확인
    public boolean checkNicknameDuplicate(String nickname) {
        return memberRepository.existsByMemberNickname(nickname);
    }

    //회원가입
    public void join(JoinRequest req) {
        String encodedPassword = passwordEncoder.encode(req.getPassword());
        memberRepository.save(req.toEntity(encodedPassword));

    }



    // 비밀번호 수정
    public void updatePassword(Long memberId, MemberRequest.Password passwordRequest) {
        // DTO에서 비밀번호와 비밀번호 확인이 일치하는지 검증
        if (!passwordRequest.getPassword().equals(passwordRequest.getCheckPassword())) {
            throw new IllegalArgumentException("비밀번호와 비밀번호 확인이 일치하지 않습니다.");
        }

        Optional<Member> optionalMember = memberRepository.findByMemberId(memberId);

        optionalMember.ifPresent(member -> {
            // 비밀번호 암호화
            member.updatePassword(passwordEncoder.encode(passwordRequest.getPassword()));

            memberRepository.save(member);
        });

    }

    // 닉네임 변경
    public void updateNickname(Long memberId, MemberRequest.Nickname nicknameRequest) {
        // memberId로 DB에서 해당 회원 정보 조회
        Optional<Member> optionalMember = memberRepository.findByMemberId(memberId);

        optionalMember.ifPresent(member -> {
            // 비밀번호 암호화
            member.updateNickname(nicknameRequest.getNickName());

            memberRepository.save(member);
        });
    }

    // 주소지 입력
    public void updateAddress(Long memberId, MemberRequest.Address addressRequest) {
        // memberId로 DB에서 해당 회원 정보 조회
        Optional<Member> optionalMember = memberRepository.findByMemberId(memberId);
        optionalMember.ifPresent(member -> {

            member.createNameAndPhoneAndAddress(
                    addressRequest.getName(),
                    addressRequest.getPhone(),
                    addressRequest.getAddress());

            memberRepository.save(member);
        });

    }



    // 생일, 성별 변경
    public void updateBirthGender(Long memberId, MemberRequest.BirthGender BirthGenderRequest) {
        // memberId로 DB에서 해당 회원 정보 조회
        Optional<Member> optionalMember = memberRepository.findByMemberId(memberId);
        optionalMember.ifPresent(member -> {
            member.updateBirthGender(
                    BirthGenderRequest.getGender()
                    ,BirthGenderRequest.getBirth()
            );

            memberRepository.save(member);
        });
    }

    //비밀번호 찾기
    //메일 전송 DTO 생성 및 비번 업데이트
    public EmailResponseDTO.sendPwDto createMailUpdatePW(String userEmail) {
        EmailResponseDTO.sendPwDto responseDTO = new EmailResponseDTO.sendPwDto();
        Optional<Member> optionalMember=memberRepository.findByEmail(userEmail);

        String pw = registerMail.createCode();
        responseDTO.setAddress(userEmail);
        responseDTO.setTitle("냥가왈부 임시비밀번호 안내 메일입니다.");
        responseDTO.setMessage("안녕하세요. 세상 모든 반려인들을 위한 서비스 냥가왈부 입니다. " +
                "\n\n\n 회원님의 비밀번호는 < "+pw+" >입니다. " +
                "\n 로그인하신 후 꼭 비밀번호를 변경해 주세요. " +
                "\n\n 비밀번호 변경은 마이페이지>메뉴>비밀번호 변경 에 있습니다. ");

        //pw 업데이트
        optionalMember.ifPresent(member -> {
            // 비밀번호 암호화후 저장
            member.updatePassword(passwordEncoder.encode(pw));
            memberRepository.save(member);
        });

        return responseDTO;

    }
    // 프로필 이미지 설정
    @Transactional
    public String updateProfileImage(MultipartFile file, String dirPath) {
        return objectStorageService.uploadFile(Singleton.S3_BUCKET_NAME, dirPath, file);
    }
    @Transactional
    public void profileSave(MemberImgRequest dto) {
        Optional<Member> optionalMember=memberRepository.findByMemberId(dto.getMemberId());

        optionalMember.ifPresent(member -> {
            member.saveProfile(dto.getImgUrl());

            memberRepository.save(member);
        });

    }

    //일반 로그인 -안씀
//    public Member login(String email, String password) {
//
//        Optional<Member> member = memberRepository.findByEmail(email);
//
//        if (member.isEmpty()) {
//            log.info("그런 사람 없습니다.");
//            return null;
//        }else {
//            Member member1 = member.get();
//
//            // passwordEncoder.matches를 사용하여 비밀번호 비교
//            if(passwordEncoder.matches(password, member1.getPassword())){
//                return member1;
//            }
//        }
//        log.info("비번 잘못 쳤습니다.");
//        return null;
//    }
}
