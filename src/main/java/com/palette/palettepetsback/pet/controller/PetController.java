package com.palette.palettepetsback.pet.controller;

import com.palette.palettepetsback.config.jwt.AuthInfoDto;
import com.palette.palettepetsback.config.jwt.JWTUtil;
import com.palette.palettepetsback.config.jwt.jwtAnnotation.JwtAuth;
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
    @GetMapping("/list")
    public List<PetResponseDto> getPetListByMemberId(@JwtAuth AuthInfoDto authInfoDto) {
        return petService.findAllByMemberId(authInfoDto.getMemberId());
    }

    // 접근 회원이 반려 동물의 주인이 맞는 지 확인
    @GetMapping("/{petId}/checkMaster")
    public boolean checkIsMaster(@PathVariable("petId") Long petId,
                                        @JwtAuth AuthInfoDto authInfoDto) {
        return petService.checkIsMaster(petId, authInfoDto.getMemberId());
    }

    // 등록된 반려동물 정보 상세 정보 쿼리
    @GetMapping("/{petId}")
    public PetResponseDto getPetByPetId(@PathVariable("petId") Long petId,
                                        @JwtAuth AuthInfoDto authInfoDto) {
        return petService.findByPetId(petId);
    }

    // 펫 이미지 리스트 가져오기
    @GetMapping("/img/list/{petId}")
    public List<ImgPetResponseDto> getPetImgListByPetId(@PathVariable("petId") Long petId,
                                                        @JwtAuth AuthInfoDto authInfoDto) {
        return petService.findAllPetImgById(petId);
    }

    // 펫 등록
    @PostMapping("")
    public boolean registerPet(@Validated @RequestPart("dto") PetRegistryDto dto,
                               @RequestPart("file") MultipartFile file,
                               @JwtAuth AuthInfoDto authInfoDto) {
        // memberId 넣기
        dto.setCreatedWho(authInfoDto.getMemberId());

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
                             @RequestPart(value = "file", required = false) MultipartFile file,
                             @JwtAuth AuthInfoDto authInfoDto) {
        // memberId 넣기
        dto.setCreatedWho(authInfoDto.getMemberId());

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
}
