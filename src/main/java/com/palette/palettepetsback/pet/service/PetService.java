package com.palette.palettepetsback.pet.service;

import com.palette.palettepetsback.config.exceptions.NoSuchPetException;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;
    private final ImgPetRepository imgPetRepository;

    // 펫 등록
    @Transactional
    public boolean registerPet(PetRegistryDto dto) {

        Pet saved = petRepository.save(
                Pet.builder()
                        .createdWho(dto.getCreatedWho())
                        .petName(dto.getPetName())
                        .petImage(dto.getPetImage())
                        .petCategory1(dto.getPetCategory1())
                        .petCategory2(dto.getPetCategory2())
                        .petBirth(dto.getPetBirth())
                        .petGender(dto.getPetGender())
                        .petWeight(dto.getPetWeight())
                        .build()
        );

        return saved.getId() != null;
    }

    // 등록된 펫 관련 이미지 등록 TODO - 나중에 S3로 저장하는 코드 작성하기
    @Transactional
    public boolean registerImgPet(ImgPetRegistryDto dto) {

        Pet pet = petRepository.findById(dto.getPetId()).orElseThrow(() -> new NoSuchPetException("pet not found"));

        ImgPet saved = imgPetRepository.save(
                ImgPet.builder()
                        .imgUrl(dto.getImgUrl())
                        .pet(pet)
                        .build()
        );

        return saved.getId() != null;
    }

    // 펫 등록 정보 수정
    @Transactional
    public void updatePet(PetUpdateDto dto) {
        Pet pet = petRepository.findById(dto.getPetId())
                .orElseThrow(() -> new NoSuchPetException("pet not found"));
        // dirty checking
        pet.updatePet(dto);
    }

    // 펫 등록 정보 삭제 -> 물리적 삭제
    @Transactional
    public boolean deletePet(Long petId) {
        petRepository.deleteById(petId);
        Pet pet = petRepository.findById(petId).orElse(null);
        return pet == null;
    }

    // 펫 등록 정보 -> 펫 이미지 삭제 (다중)

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
                .createdWho(pet.getCreatedWho())
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
                    .createdWho(pet.getCreatedWho())
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
}
