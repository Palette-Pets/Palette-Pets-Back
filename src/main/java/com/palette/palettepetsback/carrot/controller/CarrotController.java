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
    public ResponseEntity<?> create(@ModelAttribute CarrotRequestDTO dto,
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
        System.out.println("dto = " + dto.getCarrotTitle());
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

            CarrotImage carrotImage = new CarrotImage();
            carrotImage.setCarrotImageUrl(carrotImg);
            //dto.setCarrotImg(carrotImg);
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

    //전체 리스트
    @GetMapping("/test")
    public List<CarrotResponseDTO> test() {
        List<CarrotResponseDTO> carrotList = carrotService.test();
        //System.out.println(carrotList);
        log.info("carrot {}", carrotList);

        return carrotList;
    }

    //페이징, 정렬 처리 된 리스트
    @GetMapping("/list")
    public ResponseEntity<List<CarrotResponseDTO>> List(@ModelAttribute PageableDTO pd) {
        List<CarrotResponseDTO> carrots = carrotService.getList(pd);
        log.info("carrot = {}", carrots);
        return ResponseEntity.ok().body(carrots);
    }

    //상세 보기 & 조회수 증가
    @GetMapping("/list/{id}")
    public ResponseEntity<CarrotResponseDTO> view(@PathVariable Long id) {
        CarrotResponseDTO carrot = carrotService.listDetail(id);
        //상세 보기를 누르면 자동으로 조회수 증가
        carrotService.updateView(id);

        return ResponseEntity.ok().body(carrot);
    }

    //좋아요 클릭
    // 상세 페이지에서만 누를 수 있습니다.
    @PostMapping("/like/{id}")
    public ResponseEntity<?> like(@PathVariable(name = "id") Long id,
                                  @JwtAuth AuthInfoDto authInfoDto) {
        Long memberId= authInfoDto.getMemberId();

        return ResponseEntity.ok(carrotService.like(id,memberId));
    }

    //멤버별 좋아요 누른 당근 리스트
    @GetMapping("/like")
    public List<CarrotResponseDTO> getLike(@JwtAuth AuthInfoDto authInfoDto) {
        Long memberId= authInfoDto.getMemberId();
        return carrotService.getLike(memberId);
    }

    //검색 기능
    @GetMapping("/search")
    public List<CarrotResponseDTO> searchCarrots(@RequestParam String keyword) {

        return carrotService.searchCarrots(keyword);
    }

    //네이버 공유 기능
    @GetMapping("http://share.naver.com/web/shareView")
    public boolean naverShare() {
        return true;
    }

}
