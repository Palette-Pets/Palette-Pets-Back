package com.palette.palettepetsback.member.controller;

import com.palette.palettepetsback.config.Mail.EmailResponseDTO;
import com.palette.palettepetsback.config.Mail.RegisterMail;
import com.palette.palettepetsback.config.security.CustomUserDetails;
import com.palette.palettepetsback.member.dto.JoinRequest;
import com.palette.palettepetsback.member.dto.MemberImgRequest;
import com.palette.palettepetsback.member.dto.MemberRequest;
import com.palette.palettepetsback.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final RegisterMail registerMail;

    private static Long getMemberId(Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Long memberId = userDetails.getMember().getMemberId();
        return memberId;
    }


    @PostMapping("/join")
    public ResponseEntity<String> signup (@Valid @RequestBody JoinRequest joinRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) { //에러출력
            List<FieldError> list = bindingResult.getFieldErrors();
            for(FieldError error : list) {
                return new ResponseEntity<>(error.getDefaultMessage() , HttpStatus.BAD_REQUEST);
            }
        }
        if (memberService.checkEmailDuplicate(joinRequest.getEmail())) {
            return ResponseEntity.badRequest().body("이미 존재하는 이메일입니다.");
        }
        memberService.join(joinRequest);
        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    //비밀번호 찾기
    //등록된 이메일로 임시비밀번호를 발송하고 발송된 임시비밀번호로 사용자의 pw를 변경하는 컨트롤러
    @PostMapping("/memberF/findPw")
    public ResponseEntity<String>sendEmail(@RequestBody  MemberRequest.Email Email){
        if (memberService.checkEmailDuplicate(Email.getEmail())) { //이메일 존재
            EmailResponseDTO.sendPwDto dto = memberService.createMailUpdatePW(Email.getEmail());

            registerMail.mailSend(dto);

            return ResponseEntity.ok("이메일이 전송되었습니다. 메일함에서 확인해 주세요.");
        } else {
            return ResponseEntity.badRequest().body("해당하는 이메일이 없습니다. 다시 입력해 주세요.");
        }
    }


    //닉네임 중복확인 버튼
    //중복시 true 반환
    @PostMapping("/memberF/checknickname")
    public Boolean checkNickname(@RequestBody MemberRequest.Nickname nickname) {
        log.info("nick={}",nickname.getNickName());
        if (memberService.checkNicknameDuplicate(nickname.getNickName())) {
            return true; // 중복되면 true 반환
        }
        return false; // 중복되지 않으면 false 반환
    }


    // 비밀번호 수정
    @PutMapping("/member/password")
    public ResponseEntity<String> updatePassword(@Valid @RequestBody MemberRequest.Password passwordRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) { // 에러 출력
            List<FieldError> list = bindingResult.getFieldErrors();
            for (FieldError error : list) {
                return new ResponseEntity<>(error.getDefaultMessage(), HttpStatus.BAD_REQUEST);
            }
        }

        // SecurityContext에서 인증 정보 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            Long memberId = getMemberId(authentication);
            // 서비스 메서드 호출하여 비밀번호 업데이트
            memberService.updatePassword(memberId, passwordRequest);
        } else {
            return ResponseEntity.badRequest().body("인증되지 않은 사용자입니다.");
        }




        return ResponseEntity.ok("비밀번호가 수정되었습니다.");
    }

    // 닉네임 변경
    @PutMapping("/member/nickname")
    public ResponseEntity<String> updateNickname(@Valid @RequestBody MemberRequest.Nickname nicknameRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) { // 에러 출력
            List<FieldError> list = bindingResult.getFieldErrors();
            for (FieldError error : list) {
                return new ResponseEntity<>(error.getDefaultMessage(), HttpStatus.BAD_REQUEST);
            }
        }
        //닉네임 중복 확인
        if (memberService.checkNicknameDuplicate(nicknameRequest.getNickName())) {
            return ResponseEntity.badRequest().body("이미 존재하는 닉네임입니다.");
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            Long memberId = getMemberId(authentication);

            memberService.updateNickname(memberId,nicknameRequest);
        } else {
            return ResponseEntity.badRequest().body("인증되지 않은 사용자입니다.");
        }

        return ResponseEntity.ok("닉네임이 수정되었습니다.");
    }


    // 주소지 입력 -> 실명, 폰번호, 주소
    @PutMapping("/member/address")
    public ResponseEntity<String> updateAddress(@Valid @RequestBody MemberRequest.Address addressRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) { // 에러 출력
            List<FieldError> list = bindingResult.getFieldErrors();
            for (FieldError error : list) {
                return new ResponseEntity<>(error.getDefaultMessage(), HttpStatus.BAD_REQUEST);
            }
        }
        // SecurityContext에서 인증 정보 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            Long memberId = getMemberId(authentication);

            memberService.updateAddress(memberId, addressRequest);
        } else {
            return ResponseEntity.badRequest().body("인증되지 않은 사용자입니다.");
        }

        return ResponseEntity.ok("주소지가 입력되었습니다.");
    }



    // 생일, 성별 변경
    @PutMapping("/member/other")
    public ResponseEntity<String> updateBirthGender(@Valid @RequestBody MemberRequest.BirthGender birthGenderRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) { // 에러 출력
            List<FieldError> list = bindingResult.getFieldErrors();
            for (FieldError error : list) {
                return new ResponseEntity<>(error.getDefaultMessage(), HttpStatus.BAD_REQUEST);
            }
        }
        // SecurityContext에서 인증 정보 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            Long memberId = getMemberId(authentication);

            memberService.updateBirthGender(memberId, birthGenderRequest);
        } else {
            return ResponseEntity.badRequest().body("인증되지 않은 사용자입니다.");
        }

        return ResponseEntity.ok("생일과 성별이 입력되었습니다.");
    }


    // 프로필 이미지 설정
    //이미지 해상도 자동으로 변경해주는 로직이 추가 .. 예정,,
    @PostMapping("/member/image")
    public ResponseEntity<String> updateProfileImage(
                                  @RequestPart("files") MultipartFile file) {
        // SecurityContext에서 인증 정보 추출
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            Long memberId = getMemberId(authentication);
            try {
                MemberImgRequest dto = new MemberImgRequest();
                String imageUrl = memberService.updateProfileImage(file, "member/Profile");
                dto.setImgUrl(imageUrl);
                dto.setMemberId(memberId);

                memberService.profileSave(dto);

                return ResponseEntity.ok("프로필 이미지가 수정되었습니다.");
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.internalServerError().body("프로필 이미지 업데이트에 실패하였습니다.");
            }
        } else {
            return ResponseEntity.badRequest().body("인증되지 않은 사용자입니다.");
        }

    }
    @GetMapping("/test")
    public ResponseEntity<?> test() {

        return new ResponseEntity<>("테스트입니다", HttpStatus.OK);
    }
    //로그인 페이지 - 사용하지 않습니다. -> loginFilter로 가세요
//    @GetMapping("/login")
//    public String loginPage() {
//
//        return "login";
//    }
//    @PostMapping("/login")
//    public ResponseEntity<String> login(@Valid @RequestBody LoginRequest loginRequest, BindingResult bindingResult) {
//
//        if (bindingResult.hasErrors()) { //에러출력
//            List<FieldError> list = bindingResult.getFieldErrors();
//            for(FieldError error : list) {
//                return new ResponseEntity<>(error.getDefaultMessage() , HttpStatus.BAD_REQUEST);
//            }
//        }
//        //보안상 에러메시지는 간소화 했습니다.
//        if (memberService.login(loginRequest.getUsername(), loginRequest.getPassword())==null) {
//            return ResponseEntity.badRequest().body("이메일 또는 비밀번호가 잘못되었습니다.");
//        }
//
//        return ResponseEntity.ok("로그인 성공");
//    }
}
