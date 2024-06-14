package com.palette.palettepetsback.carrot.controller;

import com.palette.palettepetsback.Article.articleView.DTO.PageableDTO;
import com.palette.palettepetsback.carrot.domain.Carrot;
import com.palette.palettepetsback.carrot.domain.CarrotImage;
import com.palette.palettepetsback.carrot.dto.CarrotImageDTO;
import com.palette.palettepetsback.carrot.dto.CarrotRequestDTO;
import com.palette.palettepetsback.carrot.dto.CarrotResponseDTO;
import com.palette.palettepetsback.carrot.service.CarrotService;
import com.palette.palettepetsback.config.jwt.AuthInfoDto;
import com.palette.palettepetsback.config.jwt.jwtAnnotation.JwtAuth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/carrot")
@RequiredArgsConstructor
public class CarrotController {
    private final CarrotService carrotService;

    //글 & 이미지 등록
    @PostMapping("/post")
    public ResponseEntity<?> create(@RequestBody CarrotRequestDTO dto,
                         @RequestPart(required = false, name = "files") MultipartFile[] files,
                         @JwtAuth AuthInfoDto authInfoDto) {
        //security 부터 memberId 값 받아오기
        Long memberId = authInfoDto.getMemberId();

        //글 등록
        Carrot carrot = carrotService.create(dto, memberId);

        //파일 업로드 및 이미지 저장
        for(MultipartFile file : files) {
            String carrotImageUrl = carrotService.fileUpload(file, "carrot/img");
            carrotService.saveImg(carrotImageUrl, carrot);
        }

        return ResponseEntity.ok("글 등록 완료");
    }

    //글 & 이미지 수정
    @PutMapping("/update/{id}")
    public ResponseEntity<Carrot> update(@PathVariable Long id,
                                         @RequestBody CarrotRequestDTO dto,
                                         @RequestPart(required = false, value = "file") MultipartFile file) {
        //이미지 수정
        if(file != null) {
            String carrotImg = carrotService.fileUpload(file, "carrot");
            dto.setCarrotImg(carrotImg);
        }
        //글 수정
        Carrot update = carrotService.update(id, dto);

        return  ResponseEntity.ok().body(update);
    }

    //글 & 이미지 삭제
    @DeleteMapping("/delete/{id}")
    public boolean delete(@PathVariable Long id) {
        //글 삭제
        carrotService.delete(id);
        //이미지 삭제
        carrotService.deleteImg(id);

        return true;
    }

    //리스트
    @GetMapping("/test")
    public List<Carrot> test() {
        List<Carrot> carrotList = carrotService.test();
        //System.out.println(carrotList);
        log.info("carrot {}", carrotList);

        return carrotList;
    }

    //리스트 출력(페이징 처리)
    @GetMapping("/list")
    public ResponseEntity<List<CarrotResponseDTO>> List(@ModelAttribute PageableDTO pd) {
        List<CarrotResponseDTO> carrots = carrotService.getList(pd);
        return ResponseEntity.ok().body(carrots);
    }

}
