package com.palette.palettepetsback.pet.service;

import com.palette.palettepetsback.config.SingleTon.Singleton;
import com.palette.palettepetsback.config.Storage.NCPObjectStorageService;
import com.palette.palettepetsback.config.exceptions.NoSuchPetException;
import com.palette.palettepetsback.member.entity.Member;
import com.palette.palettepetsback.member.repository.MemberRepository;
import com.palette.palettepetsback.pet.dto.request.ImgPetRegistryDto;
import com.palette.palettepetsback.pet.dto.request.PetRegistryDto;
import com.palette.palettepetsback.pet.dto.request.PetUpdateDto;
import com.palette.palettepetsback.pet.dto.response.ImgPetResponseDto;
import com.palette.palettepetsback.pet.dto.response.PetResponseDto;
import com.palette.palettepetsback.pet.entity.ImgPet;
import com.palette.palettepetsback.pet.entity.Pet;
import com.palette.palettepetsback.pet.repository.ImgPetRepository;
import com.palette.palettepetsback.pet.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PetService {

    private final MemberRepository memberRepository;
    private final PetRepository petRepository;
    private final ImgPetRepository imgPetRepository;
    private final NCPObjectStorageService objectStorageService;

    // 파일 저장
    @Transactional
    public String fileUpload(MultipartFile file, String dirPath) {
        return objectStorageService.uploadFile(Singleton.S3_BUCKET_NAME, dirPath, file);
    }

    // S3 파일
    @Transactional
    public String fileDelete(String fileName) {
        return objectStorageService.deleteFile(Singleton.S3_BUCKET_NAME, fileName);
    }

    // 펫 등록
    @Transactional
    public Long registerPet(PetRegistryDto dto) {

        log.info("dto = {}", dto.toString());

        Member member = memberRepository.findById(dto.getCreatedWho())
                .orElseThrow(() -> new RuntimeException("member not found"));

        String substring = dto.getPetBirth().substring(0, 17);
        dto.setPetBirth(substring);
        log.info("substring = {}", substring);

        // 펫 등록
        Pet saved = petRepository.save(
                Pet.builder()
                        .member(member)
                        .petName(dto.getPetName())
                        .petImage(dto.getPetImage())
                        .petCategory1(dto.getPetCategory1())
                        .petCategory2(dto.getPetCategory2())
                        .petBirth(dto.getPetBirth())
                        .petGender(dto.getPetGender())
                        .petWeight(dto.getPetWeight())
                        .build()
        );

        log.info("saved = {}", saved);

        return saved.getId();
    }

    // 등록된 펫 관련 이미지 등록 TODO - 나중에 S3로 저장하는 코드 작성하기
    @Transactional
    public Long registerImgPet(ImgPetRegistryDto dto) {

        Pet pet = petRepository.findById(dto.getPetId()).orElseThrow(() -> new NoSuchPetException("pet not found"));

        ImgPet saved = imgPetRepository.save(
                ImgPet.builder()
                        .imgUrl(dto.getImgUrl())
                        .pet(pet)
                        .build()
        );

        return saved.getId();
    }

    // 펫 등록 정보 수정
    @Transactional
    public void updatePet(PetUpdateDto dto) {

        Pet pet = petRepository.findById(dto.getPetId())
                .orElseThrow(() -> new NoSuchPetException("pet not found"));

        String substring = dto.getPetBirth().substring(0, 17);
        dto.setPetBirth(substring);

        // dirty checking
        pet.updatePet(dto);
    }

    // 펫 등록 정보 삭제 -> 물리적 삭제
    @Transactional
    public void deletePet(Long petId) {
        Pet pet = petRepository.findById(petId).orElseThrow(() -> new NoSuchPetException("pet not found"));
        // s3 저장된 이미지 삭제
        String fileDeleted = fileDelete(pet.getPetImage());
        log.info("NCP Object Storage file deleted = {}", "pet/" + fileDeleted);
        petRepository.deleteById(petId); // JPA cascade 로 imgPet 에 연관된 이미지도 삭제
    }

    // 펫 등록 정보 -> 펫 이미지 삭제
    @Transactional
    public void deleteImgPet(Long imgId) {
        ImgPet imgPet = imgPetRepository.findById(imgId).orElseThrow(() -> new NoSuchPetException("반려동물 이미지가 존재하지 않습니다"));
        // s3 저장된 이미지 삭제
        String fileDeleted = fileDelete("pet/img/" + imgPet.getImgUrl());
        log.info("NCP Object Storage file deleted = {}", fileDeleted);
        imgPetRepository.deleteById(imgId);
    }

    // todo queryDsl 최적화 할 수 있을 듯
    // 펫 정보 가져오기 (한건)
    public PetResponseDto findByPetId(Long petId) {

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new NoSuchPetException("pet not found"));

        // 펫 이미지 dto 리스트 생성
        ArrayList<ImgPetResponseDto> imgList = new ArrayList<>();
        pet.getPetImageList().forEach(img ->
            imgList.add(
                    ImgPetResponseDto.builder()
                            .imgPetId(img.getId())
                            .imgUrl(img.getImgUrl())
                            .petId(pet.getId())
                            .build()
            )
        );

        return PetResponseDto.builder()
                .petId(pet.getId())
                .createdWho(pet.getMember().getMemberId())
                .petName(pet.getPetName())
                .petImage(pet.getPetImage())
                .petCategory1(pet.getPetCategory1())
                .petCategory2(pet.getPetCategory2())
                .petBirth(pet.getPetBirth())
                .petGender(pet.getPetGender())
                .petWeight(pet.getPetWeight())
                .petImgList(imgList)
                .build();
    }

    // todo queryDsl 최적화 할 수 있을 듯
    // 펫 정보 가져오기 (한 회원에 연결된 펫 List)
    public List<PetResponseDto> findAllByMemberId(Long memberId) {

        List<Pet> petList = petRepository.findByCreatedWho(memberId);
        // 보낼 dto list 생성
        List<PetResponseDto> petResponseDtoList = new ArrayList<>();

        petList.forEach(pet -> {
            // 하나의 펫 정보에 들어갈 펫 이미지 리스트
            ArrayList<ImgPetResponseDto> imgPetList = new ArrayList<>();
            // 펫 정보에 들어갈 이미지 리스트들 추출
            pet.getPetImageList().forEach(img -> {
                imgPetList.add(
                        ImgPetResponseDto.builder()
                                .imgPetId(img.getId())
                                .imgUrl(img.getImgUrl())
                                .petId(img.getPet().getId())
                                .build()
                );
            });

            // 펫 정보를 반환할 dto로 변환해서 리스트에 저장
            PetResponseDto dto = PetResponseDto.builder()
                    .petId(pet.getId())
                    .createdWho(pet.getMember().getMemberId())
                    .petName(pet.getPetName())
                    .petImage(pet.getPetImage())
                    .petCategory1(pet.getPetCategory1())
                    .petCategory2(pet.getPetCategory2())
                    .petBirth(pet.getPetBirth())
                    .petGender(pet.getPetGender())
                    .petWeight(pet.getPetWeight())
                    .petImgList(imgPetList)
                    .build();

            petResponseDtoList.add(dto);
        });

        return petResponseDtoList;
    }

    public List<ImgPetResponseDto> findAllPetImgById(Long petId) {

        List<ImgPet> petImgList = imgPetRepository.findAllByPetId(petId);
        return petImgList.stream()
                .map(ImgPetResponseDto::toDto)
                .toList();
    }

    // 회원 == 주인 체크 메서드 -> 캐싱
    @Cacheable(value = "checkIsMaster", key = "#checkingMemberId", cacheManager = "cacheManager")
    public boolean checkIsMaster(Long petId, Long checkingMemberId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new NoSuchPetException("pet not found"));
        Long memberId = pet.getMember().getMemberId();
        return memberId.equals(checkingMemberId);
    }
}
