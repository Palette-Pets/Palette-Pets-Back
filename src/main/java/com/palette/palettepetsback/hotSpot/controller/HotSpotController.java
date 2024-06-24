package com.palette.palettepetsback.hotSpot.controller;

import com.palette.palettepetsback.config.SingleTon.ViewerLimit;
import com.palette.palettepetsback.config.aop.notification.NeedNotification;
import com.palette.palettepetsback.config.aop.notification.NotificationThreadLocal;
import com.palette.palettepetsback.config.jwt.AuthInfoDto;
import com.palette.palettepetsback.config.jwt.jwtAnnotation.JwtAuth;
import com.palette.palettepetsback.hotSpot.dto.request.HotSpotAddRequest;
import com.palette.palettepetsback.hotSpot.dto.request.HotSpotStarPointAddRequest;
import com.palette.palettepetsback.hotSpot.dto.request.HotSpotUpdateRequest;
import com.palette.palettepetsback.hotSpot.dto.response.HotSpotListResponse;
import com.palette.palettepetsback.hotSpot.dto.response.HotSpotRecentDTO;
import com.palette.palettepetsback.hotSpot.dto.response.HotSpotResponse;
import com.palette.palettepetsback.hotSpot.dto.response.ImgHotSpotResponse;
import com.palette.palettepetsback.hotSpot.service.HotSpotService;
import com.palette.palettepetsback.member.dto.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/hotspot")
@RequiredArgsConstructor
public class HotSpotController {

    private final HotSpotService hotSpotService;
    private final ViewerLimit viewerLimit;


    // 게시글 추가 (파일 포함)
    @PostMapping
    @NeedNotification
    public ResponseEntity<Boolean> addHotSpot(@Valid @RequestPart("request") HotSpotAddRequest request,
                                           @RequestPart(value = "files", required = false) MultipartFile[] files,
                                           @JwtAuth AuthInfoDto authInfoDto) {
        request.setMemberId(authInfoDto.getMemberId());
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

        return ResponseEntity.ok(true);
    }

    // 게시글 업데이트
    @PutMapping("/{id}")
    @NeedNotification
    public ResponseEntity<Boolean> updateHotSpot(@PathVariable("id") Long id,
                                              @Validated @RequestPart("request") HotSpotUpdateRequest request,
                                              @RequestPart(value = "files", required = false) MultipartFile[] files,
                                              @JwtAuth AuthInfoDto authInfoDto) {
        hotSpotService.HotSpotUpdate(request, files);
        NotificationThreadLocal.setNotificationInfo(authInfoDto.getMemberId(),
                "명소 추천 글을 수정했습니다",
                113);
        return ResponseEntity.ok(true);
    }

    // 게시글 삭제
    @DeleteMapping("/{id}")
    @NeedNotification
    public boolean deleteHotSpot(@PathVariable("id") Long id,
                                 @JwtAuth AuthInfoDto authInfoDto) {
        hotSpotService.HotSpotDelete(id);
        NotificationThreadLocal.setNotificationInfo(authInfoDto.getMemberId(),
                "명소 추천 글을 삭제했습니다",
                112);
        return true;
    }

    // 모든 게시글 조회
    @GetMapping("/list")
    public List<HotSpotListResponse> getAllHotSpots() {
        return hotSpotService.getAllHotSpot();
    }

    // 특정 게시글 조회
    @GetMapping("/{id}")
    public HotSpotResponse getHotSpotDetail(@PathVariable("id") Long id,
                                            HttpServletRequest request) {
        // 조회할 게시글 조회수 + 1
        if (viewerLimit.viewLimit(request)) {
            hotSpotService.plusCountView(id);
        }
        return hotSpotService.getHotSpotWithImg(id);
    }

    // 특정 게시글 이미지 리스트 조회
    @GetMapping("/{id}/img")
    public List<ImgHotSpotResponse> getHotSpotImg(@PathVariable("id") Long id) {
        return hotSpotService.getAllImgHotSpotByHotSpotId(id);
    }

    // 매니저인지 확인 요청
    @GetMapping("/checkManager")
    public Boolean checkManager(@JwtAuth AuthInfoDto authInfoDto) {
        Role role = authInfoDto.getRole();
        if (role.equals(Role.ADMIN)) {
            return true;
        } else {
            return false;
        }
    }

    // 별점 등록 요청
    @PostMapping("/rating")
    public boolean starRating(@RequestBody HotSpotStarPointAddRequest dto,
                              @JwtAuth AuthInfoDto authInfoDto) {
        // 토큰에서 필수 값 가져오기
        dto.setMemberId(authInfoDto.getMemberId());
        return hotSpotService.saveHotSpotStarPoint(dto);
    }

    // 별점 정보 가져오기
    @GetMapping("/rating/{hotSpotId}")
    public Integer starRating(@PathVariable("hotSpotId") Long hotSpotId,
                              @JwtAuth AuthInfoDto authInfoDto) {

        return hotSpotService.getHotSpotStarPoint(hotSpotId, authInfoDto.getMemberId());
    }


    // 메인페이지 최신순 가져오기
    @GetMapping("/main")
    public ResponseEntity<List<HotSpotRecentDTO>> getHotSpotRecent() {
        return ResponseEntity.ok().body(hotSpotService.getHotSpotRecent());
    }
}