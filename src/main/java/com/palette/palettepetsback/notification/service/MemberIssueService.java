package com.palette.palettepetsback.notification.service;

import com.palette.palettepetsback.config.exceptions.NoMemberExistException;
import com.palette.palettepetsback.member.entity.Member;
import com.palette.palettepetsback.member.repository.MemberRepository;
import com.palette.palettepetsback.notification.domain.MemberIssue;
import com.palette.palettepetsback.notification.repository.EmitterRepository;
import com.palette.palettepetsback.notification.repository.MemberIssueRepository;
import kr.co.shineware.nlp.komoran.constant.DEFAULT_MODEL;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberIssueService {

    private final MemberIssueRepository memberIssueRepository;
    private final EmitterRepository emitterRepository;
    private final MemberRepository memberRepository;

    private static final Long DEFAULT_TIMEOUT = 60 * 60 * 60 * 1000L;

    public SseEmitter connect(final Long memberId, final String lastEventId) {

        // 매 연결마다 고유한 이벤트 id 부여
        String eventId = memberId + "_" + System.currentTimeMillis();
        // SseEmitter 인스턴스 생성 후 Map 저장
        SseEmitter emitter = emitterRepository.save(eventId, new SseEmitter(DEFAULT_TIMEOUT));

        // 이벤트 전송할 때.
        emitter.onCompletion(() -> {
            log.info("onCompletion callback -> emitter delete");
            emitterRepository.delete(eventId);
        });

        // 이벤트 스트림 연결이 끊겼을 때.
        emitter.onTimeout(() -> {
            log.info("onTimeout callback -> emitter delete");
            emitterRepository.delete(eventId);
        });

        // 첫 연결 시, 503 Service Unavailable 방지용 더미 이벤트 데이터 전송
        sendToClient(eventId, emitter, "NOTIFICATION_CONNECT_SUCCESS");
//        sendToClient(eventId, emitter, "알림 서버 연결 성공 [memberId = "+ memberId + "]");

        // 클라이언트가 미수신한 event 목록이 존재할 경우 전송해서 event 유실을 예방
        if (!lastEventId.isEmpty()) {
            Map<String, SseEmitter> events = emitterRepository.findAllEventCacheStartWithId(String.valueOf(memberId));
            events.entrySet().stream()
                    .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                    .forEach(entry -> sendToClient(entry.getKey(), emitter, entry.getValue()));
        }

        return emitter;
    }

    private void sendToClient(String eventId, SseEmitter emitter, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .name("notification")
                    .id(eventId)
                    .data(data));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 다른 서비스 클래스에서 이벤트가 발생했을 때, 알림을 보내는 메서드
    @Async
    @Transactional
    public void sendNotification(final Long memberId, final String issueContent, final Integer issueCode) {

        Member recipient = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new NoMemberExistException("회원이 존재하지 않습니다"));

        MemberIssue notification = MemberIssue.builder()
                .receiver(recipient)
                .issueContent(issueContent)
                .issueCode(issueCode)
                .build();

        MemberIssue memberIssue = memberIssueRepository.save(notification);
        String eventId = memberIssue.getReceiver().getMemberId() + "_" + System.currentTimeMillis();

        // 유저의 모든 SseEmitter 가져오기
        Map<String, SseEmitter> sseEmitters = emitterRepository.findAllEventCacheStartWithId(recipient.getMemberId().toString());
        sseEmitters.forEach(
                (key, emitter) -> {
                    try {
                       // 데이터 전송
                        sendToClient(eventId, emitter, memberIssue.getIssueContent());
                        log.info("send emitter to client successfully. notification = {}", memberIssue);
                    } catch (Exception e) {
                        // 실패한 emitter 제거
                        emitterRepository.delete(key);
                        log.error("Failed to send issue notification", e);
                    }
                }
        );
    }

    @Transactional
    public void readMemberIssue(Long memberIssueId) {

        MemberIssue memberIssue = memberIssueRepository.findById(memberIssueId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 알림입니다"));

        // 해당 알림을 읽음표시
        memberIssue.changeIsRead();
    }



}
