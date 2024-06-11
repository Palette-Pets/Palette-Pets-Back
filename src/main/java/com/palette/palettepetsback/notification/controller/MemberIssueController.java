package com.palette.palettepetsback.notification.controller;

import com.palette.palettepetsback.config.aop.notification.NeedNotification;
import com.palette.palettepetsback.config.aop.notification.NotificationThreadLocal;
import com.palette.palettepetsback.config.jwt.AuthInfoDto;
import com.palette.palettepetsback.config.jwt.jwtAnnotation.JwtAuth;
import com.palette.palettepetsback.member.entity.Member;
import com.palette.palettepetsback.member.repository.MemberRepository;
import com.palette.palettepetsback.notification.service.MemberIssueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Random;

@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberIssueController {
    // 알람 기능 컨트롤러

    private final MemberIssueService memberIssueService;
    private final MemberRepository memberRepository;

    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> connect(
            @RequestHeader(value = "Last_Event_ID", required = false, defaultValue = "") final String lastEventId,
            @JwtAuth final AuthInfoDto authInfoDto
            ) {

        return ResponseEntity.ok(memberIssueService.connect(authInfoDto.getMemberId(), lastEventId));
    }

    @GetMapping("/sse/test1")
    @NeedNotification
    public ResponseEntity<?> test1() {

        String coreFeatures = "emitter test";

        Member member = memberRepository.findById(1L).orElseThrow(() -> new RuntimeException("zz"));
        Long memberId = member.getMemberId();
        NotificationThreadLocal.setNotificationInfo(memberId, "첫 알람 테스트", 777);

        return ResponseEntity.ok(coreFeatures);
    }

    @GetMapping("/sse/test2")
    @NeedNotification
    public ResponseEntity<?> test2() {

        String coreFeatures = "emitter test22";
        // 횡단 관심사 설정
        NotificationThreadLocal.setNotificationInfo(1L, "두번째 알람 테스트", 999);

        return ResponseEntity.ok(coreFeatures);
    }

}
