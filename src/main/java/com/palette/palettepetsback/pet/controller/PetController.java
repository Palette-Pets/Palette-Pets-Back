package com.palette.palettepetsback.pet.controller;

import com.palette.palettepetsback.config.jwt.AuthInfoDto;
import com.palette.palettepetsback.config.jwt.JWTUtil;
import com.palette.palettepetsback.pet.dto.request.ImgPetRegistryDto;
import com.palette.palettepetsback.pet.dto.request.PetRegistryDto;
import com.palette.palettepetsback.pet.dto.request.PetUpdateDto;
import com.palette.palettepetsback.pet.dto.response.ImgPetResponseDto;
import com.palette.palettepetsback.pet.dto.response.PetResponseDto;
import com.palette.palettepetsback.pet.service.PetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/pet")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

    // 한 회원이 등록한 반려동물 정보 리스트 쿼리
    @GetMapping("/list/{memberId}")
    public List<PetResponseDto> getPetListByMemberId(@PathVariable("memberId") Long memberId) {
        return petService.findAllByMemberId(memberId);
    }

    // 등록된 반려동물 정보 상세 정보 쿼리
    @GetMapping("/{petId}")
    public PetResponseDto getPetByPetId(@PathVariable("petId") Long petId) {
        return petService.findByPetId(petId);
    }

    // 펫 이미지 리스트 가져오기
    @GetMapping("/img/list/{petId}")
    public List<ImgPetResponseDto> getPetImgListByMemberId(@PathVariable("petId") Long petId) {
        return petService.findAllPetImgById(petId);
    }

    // 펫 등록
    @PostMapping("")
    public boolean registerPet(@Validated @RequestPart("dto") PetRegistryDto dto,
                               @RequestPart("file") MultipartFile file) {
        // todo S3 저장 메서드 test 필요
        String petImage = petService.fileUpload(file, "pet");
        dto.setPetImage(petImage);
        log.info("dto={}", dto);
        return petService.registerPet(dto) != null;
    }

    // 펫 이미지 등록
    @PostMapping("/img")
    public boolean registerPetImg(@Validated @RequestPart("dto") ImgPetRegistryDto dto,
                                  @RequestPart(value = "files") MultipartFile[] files) {
        // todo S3 저장 메서드 + 펫 이미지 리스트 저장
        log.info("dto = {}", dto);
        log.info("files = {}", (Object[]) files);
        int i = 0;
        for (MultipartFile file : files) {
            i++;
            log.info("{}th file={}", i, file);
            String imgUrl = petService.fileUpload(file, "pet/img");
            dto.setImgUrl(imgUrl);
            Long saved = petService.registerImgPet(dto);
            if (saved == null) {
                log.info("펫 이미지 리스트 저장 중 에러 발생");
                return false;
            }
        }
        return true;
    }

    // 펫 수정 - 펫 정보만
    @PutMapping("/{petId}")
    public boolean updatePet(@PathVariable("petId") Long petId,
                             @Validated @RequestPart("dto") PetUpdateDto dto,
                             @RequestPart(value = "file", required = false) MultipartFile file) {
        if (file != null) {
            String petImage = petService.fileUpload(file, "pet");
            dto.setPetImage(petImage);
        }
        petService.updatePet(dto);
        return true;
    }

    // 펫 삭제
    @DeleteMapping("{petId}")
    public boolean deletePet(@PathVariable("petId") Long petId) {
        petService.deletePet(petId);
        return true;
    }

    // 펫 이미지 삭제
    @DeleteMapping("/img/{imgId}")
    public boolean deletePetImg(@PathVariable("imgId") Long imgId) {
        // todo S3 삭제 메서드 test 필요
        petService.deleteImgPet(imgId);
        return true;
    }

    // 이미지 업로드  테스트
    @PostMapping("/img/test")
    public boolean imgUpdateTest(@RequestPart("dto") ImgPetRegistryDto dto,
                                 @RequestPart("file") MultipartFile file) {
        String s3Result = petService.fileUpload(file, "test");
        log.info("dto ={}", dto);
        log.info("s3Result ={}", s3Result);
        return true;
    }

    // 잠깐 getMemberInfo() 호출 테스트 -> 로그인하고 테스트 or 로그인 안하고 테스트 해보기
    @GetMapping("/test/memberInfo")
    public AuthInfoDto test2() {
        AuthInfoDto memberInfo = JWTUtil.getMemberInfo();
        log.info("memberInfo={}", memberInfo);
        return memberInfo;
    }
}
