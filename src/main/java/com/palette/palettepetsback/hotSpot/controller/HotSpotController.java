package com.palette.palettepetsback.hotSpot.controller;

import com.palette.palettepetsback.config.aop.notification.NeedNotification;
import com.palette.palettepetsback.config.aop.notification.NotificationThreadLocal;
import com.palette.palettepetsback.config.jwt.AuthInfoDto;
import com.palette.palettepetsback.config.jwt.jwtAnnotation.JwtAuth;
import com.palette.palettepetsback.hotSpot.dto.request.HotSpotAddRequest;
import com.palette.palettepetsback.hotSpot.dto.request.HotSpotUpdateRequest;
import com.palette.palettepetsback.hotSpot.dto.response.HotSpotListResponse;
import com.palette.palettepetsback.hotSpot.dto.response.HotSpotResponse;
import com.palette.palettepetsback.hotSpot.dto.response.ImgHotSpotResponse;
import com.palette.palettepetsback.hotSpot.service.HotSpotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/hotspot")
public class HotSpotController {

    private final HotSpotService hotSpotService;

    public HotSpotController(HotSpotService hotSpotService) {
        this.hotSpotService = hotSpotService;
    }

    // 게시글 추가 (파일 포함)
    @PostMapping
    @NeedNotification
    public ResponseEntity<Void> addHotSpot(@Valid @RequestPart("request") HotSpotAddRequest request,
                                           @RequestPart(value = "files", required = false) MultipartFile[] files,
                                           @JwtAuth AuthInfoDto authInfoDto) {
        Long hotSpotId = hotSpotService.HotSpotInsert(request);
        // ImgHotSpot 에 이미지 저장
        for (MultipartFile file : files) {
            Long imgHotSpotId = hotSpotService.addHotSpotImage(hotSpotId, file);
            if (imgHotSpotId == null) {
                throw new RuntimeException("이미지 저장 중 오류가 났습니다");
            }
        }
        NotificationThreadLocal.setNotificationInfo(authInfoDto.getMemberId(), 
                "명소 추천 글을 작성 성공했습니다", 
                111);

        return ResponseEntity.ok().build();
    }

    // 게시글 업데이트
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateHotSpot(@PathVariable("id") Long id,
                                              @Validated @RequestBody HotSpotUpdateRequest request,
                                              @RequestPart(value = "files", required = false) MultipartFile[] files) {
        hotSpotService.HotSpotUpdate(request, files);
        return ResponseEntity.ok().build();
    }

    // 게시글 삭제
    @DeleteMapping("/{id}")
    public boolean deleteHotSpot(@PathVariable("id") Long id) {
        hotSpotService.HotSpotDelete(id);
        return true;
    }

    // 모든 게시글 조회
    @GetMapping("/list")
    public List<HotSpotListResponse> getAllHotSpots() {
        return hotSpotService.getAllHotSpot();
    }

    // 특정 게시글 조회
    @GetMapping("/{id}")
    public HotSpotResponse getHotSpotDetail(@PathVariable("id") Long id) {
        return hotSpotService.getHotSpotWithImg(id);
    }

    // 특정 게시글 이미지 리스트 조회
    @GetMapping("/{id}/img")
    public List<ImgHotSpotResponse> getHotSpotImg(@PathVariable("id") Long id) {
        return hotSpotService.getAllImgHotSpotByHotSpotId(id);
    }
}