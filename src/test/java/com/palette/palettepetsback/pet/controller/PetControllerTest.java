//package com.palette.palettepetsback.pet.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.palette.palettepetsback.config.Storage.NCPObjectStorageService;
//import com.palette.palettepetsback.pet.dto.request.ImgPetRegistryDto;
//import com.palette.palettepetsback.pet.dto.request.PetRegistryDto;
//import com.palette.palettepetsback.pet.repository.ImgPetRepository;
//import com.palette.palettepetsback.pet.repository.PetRepository;
//import com.palette.palettepetsback.pet.service.PetService;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.File;
//
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@Slf4j
//@Transactional
//@ExtendWith(MockitoExtension.class)
//class PetControllerTest {
//
//    @InjectMocks
//    private PetController petController;
//    @Mock
//    private PetService petService;
//    @Mock
//    private PetRepository petRepository;
//    @Mock
//    private ImgPetRepository imgPetRepository;
//    @MockBean
//    private NCPObjectStorageService objectStorageService;
//
//    MockMvc mockMvc;
//    ObjectMapper objectMapper = new ObjectMapper();
//
//    @BeforeEach
//    public void beforeEach() {
//        mockMvc = MockMvcBuilders.standaloneSetup(petController).build();
//    }
//
//    @Test
//    @DisplayName("펫 등록 Dto 저장 + 펫 이미지 리스트 등록 (임시로 펫 등록만)")
//    void registerPet() throws Exception {
////        PetRegistryDto dto = PetRegistryDto.builder()
////                .createdWho(1L)
////                .petName("강아쥐")
////                .petImage("https://cdn.pixabay.com/photo/2023/09/19/12/34/dog-8262506_1280.jpg")
////                .petCategory1("강아지")
////                .petCategory2("치와와")
////                .petBirth("19960124")
////                .petGender("수컷")
////                .petWeight(5)
////                .build();
////
////        MockMultipartFile image = new MockMultipartFile(
////                "petImage",
////                "dog.jpg",
////                MediaType.IMAGE_JPEG_VALUE,
////                "image content".getBytes()
////        );
////
////        // NCPObjectStorageService의 uploadFile 메서드 mocking
////        when(objectStorageService.uploadFile(anyString(), anyString(), any(MultipartFile.class)))
////                .thenReturn("mocked_file_url");
////
////        mockMvc.perform(
////                multipart("/pet")
////                        .file(image)
////                        .content(objectMapper.writeValueAsString(dto))
////                        .contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
////                )
////                .andExpect(status().isOk());
////
////        verify(petService).registerPet(any(PetRegistryDto.class));
//    }
//
//    @Test
//    @DisplayName("펫 이미지 등록 메서드")
//    void registerImgPet() throws Exception {
//        ImgPetRegistryDto dto = ImgPetRegistryDto.builder()
//                .imgUrl("test1")
//                .petId(1L)
//                .build();
//
//        mockMvc.perform(
//                post("/pet/img")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(dto)))
//                .andExpect(status().isOk());
//
//        verify(petService).registerImgPet(dto);
//    }
//
//    @Test
//    void updatePet() throws Exception {
//    }
//
//    @Test
//    void deletePet() throws Exception {
//    }
//
//    @Test
//    void deleteImgPet() throws Exception {
//    }
//
//    @Test
//    void findByPetId() throws Exception {
//    }
//
//    @Test
//    void findAllByMemberId() throws Exception {
//    }
//}