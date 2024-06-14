//package com.palette.palettepetsback.pet.service;
//
//import com.palette.palettepetsback.member.entity.Member;
//import com.palette.palettepetsback.member.repository.MemberRepository;
//import com.palette.palettepetsback.pet.dto.request.ImgPetRegistryDto;
//import com.palette.palettepetsback.pet.dto.request.PetRegistryDto;
//import com.palette.palettepetsback.pet.dto.request.PetUpdateDto;
//import com.palette.palettepetsback.pet.entity.ImgPet;
//import com.palette.palettepetsback.pet.entity.Pet;
//import com.palette.palettepetsback.pet.repository.ImgPetRepository;
//import com.palette.palettepetsback.pet.repository.PetRepository;
//import lombok.extern.slf4j.Slf4j;
//import org.assertj.core.api.Assertions;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.Optional;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyLong;
//import static org.mockito.BDDMockito.given;
//import static org.mockito.Mockito.verify;
//
//@Slf4j
//@Transactional
//@ExtendWith(MockitoExtension.class)
//class PetServiceTest {
//
//    @InjectMocks
//    PetService petService;
//    @Mock
//    PetRepository petRepository;
//    @Mock
//    ImgPetRepository imgPetRepository;
//    @Mock
//    MemberRepository memberRepository;
//
//    @Test
//    @DisplayName("펫 등록 메서드")
//    void registerPet() {
//
////        PetRegistryDto dto = PetRegistryDto.builder()
////                .createdWho(1L)
////                .petName("진돗개")
////                .petImage("이미지")
////                .petCategory1("강아지")
////                .petCategory2("진도")
////                .petBirth("19960124")
////                .petGender("수컷")
////                .petWeight(10)
////                .build();
////
////        // given - data가 나올 예상 값 미리 넣어두기
////        given(petRepository.save(any(Pet.class))).willReturn(Pet.builder()
////                .petName("진돗개")
////                .member(new Member())
////                .build());
////
////        // when - 실제 비교할 값
////        petService.registerPet(dto);
////
////        // then - when의 실제 결과 값이 given의 data 값과 같은 지 검증
////        verify(petRepository).save(any());
//    }
//
//    @DisplayName("펫 이미지 등록 메서드")
//    @Test
//    void registerImgPet() {
//        // 임의의 Pet 엔티티 생성
//        Pet pet = Pet.fullBuilder()
//                .id(1L)
//                .member(null)
//                .petName("진돗개")
//                .petImage("이미지")
//                .petCategory1("강아지")
//                .petCategory2("진도")
//                .petBirth("19960124")
//                .petGender("수컷")
//                .petWeight(10)
//                .build();
//        // Pet 엔티티 저장
//        given(petRepository.findById(anyLong())).willReturn(Optional.of(pet));
//
//        ImgPetRegistryDto dto = ImgPetRegistryDto.builder()
//                .imgUrl("이미지")
//                .petId(1L)
//                .build();
//
//        given(imgPetRepository.save(any(ImgPet.class))).willReturn(ImgPet.builder().imgUrl("이미지").build());
//
//        petService.registerImgPet(dto);
//
//        verify(imgPetRepository).save(any(ImgPet.class));
//    }
//
//    @DisplayName("펫 수정 메서드")
//    @Test
//    void updatePet() {
////        // 임의의 Pet 엔티티 생성
////        Pet pet = Pet.fullBuilder()
////                .id(1L)
////                .member(null)
////                .petName("진돗개")
////                .petImage("이미지")
////                .petCategory1("강아지")
////                .petCategory2("진도")
////                .petBirth("19960124")
////                .petGender("수컷")
////                .petWeight(10)
////                .build();
////        // Pet 엔티티 저장
////        given(petRepository.findById(anyLong())).willReturn(Optional.of(pet));
////
////        PetUpdateDto dto = PetUpdateDto.builder()
////                .petId(1L)
////                .createdWho(1L)
////                .petName("멍냥")
////                .petImage("개냥")
////                .petCategory1("개고양이")
////                .petCategory2("혼종")
////                .petBirth("19960124")
////                .petGender("수컷")
////                .petWeight(10)
////                .build();
////
////        petService.updatePet(dto);
////
////        assertThat(pet.getPetName()).isEqualTo("멍냥");
//    }
//
//    @DisplayName("펫 삭제 메서드")
//    @Test
//    void deletePet() {
//        petService.deletePet(1L);
//
//        verify(petRepository).deleteById(anyLong());
//    }
//
//    @Test
//    void deleteImgPet() {
//    }
//
//    @Test
//    void findByPetId() {
//    }
//
//    @Test
//    void findAllByMemberId() {
//    }
//}