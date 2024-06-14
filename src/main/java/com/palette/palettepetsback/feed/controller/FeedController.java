package com.palette.palettepetsback.feed.controller;

import com.palette.palettepetsback.config.jwt.AuthInfoDto;
import com.palette.palettepetsback.config.jwt.jwtAnnotation.JwtAuth;
import com.palette.palettepetsback.feed.dto.FeedListResponse;
import com.palette.palettepetsback.feed.dto.FeedRequest;
import com.palette.palettepetsback.feed.entity.Feed;
import com.palette.palettepetsback.feed.service.FeedService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class FeedController {

    private final FeedService feedService;

    @PostMapping("/feed")
    public ResponseEntity<?> savefeed(@RequestPart(name="request") @Valid FeedRequest feedRequest,
                                      @RequestPart(required = false, name="files") MultipartFile[] files,
                                      @JwtAuth AuthInfoDto authInfoDto) {

        Long memberId=authInfoDto.getMemberId();

        int i = 0;
        // 글자와 멤버 아이디로 피드 저장
        Feed feed = feedService.saveFeed(feedRequest.getText(), memberId);

        // 파일 업로드 및 FeedImg 저장
        for (MultipartFile file : files) {
            String imgUrl = feedService.fileUpload(file, "feed/img");
            feedService.saveFeedImg(imgUrl, feed);
        }
        return ResponseEntity.ok("피드 작성 완료.");

    }

    @GetMapping("/feed/{nickname}")
    public ResponseEntity<?> getFeedList(@PathVariable("nickname") String nickname){
        List<FeedListResponse> feedImg = feedService.getFeedList(nickname);
        return ResponseEntity.ok(feedImg);
    }

    @GetMapping("/feed/detail/{feedId}")
    public ResponseEntity<?> getFeedDetail(@PathVariable("feedId") Long feedId,
                                           @JwtAuth AuthInfoDto authInfoDto) {
        Long memberId = authInfoDto.getMemberId();
        return ResponseEntity.ok(feedService.getFeedDetail(feedId,memberId));
    }
    @DeleteMapping("/feed/detail/{feedId}")
    public ResponseEntity<?> deleteFeed(@PathVariable("feedId") Long feedId,
                                        @JwtAuth AuthInfoDto authInfoDto) {
        Long memberId = authInfoDto.getMemberId();
        feedService.deleteFeed(feedId,memberId);

        return ResponseEntity.ok("피드가 삭제 되었습니다.");
    }
}
