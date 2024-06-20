package com.palette.palettepetsback.Chat.Controller;

import com.palette.palettepetsback.Chat.Controller.DTO.ChatResponse;
import com.palette.palettepetsback.Chat.Controller.DTO.ChatRoomListResponse;
import com.palette.palettepetsback.Chat.Entity.ChatRoom;
import com.palette.palettepetsback.Chat.Service.ChatService;
import com.palette.palettepetsback.config.jwt.AuthInfoDto;
import com.palette.palettepetsback.config.jwt.jwtAnnotation.JwtAuth;
import com.palette.palettepetsback.member.entity.Member;
import com.palette.palettepetsback.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final MemberRepository memberRepository;

    @GetMapping("/api/chat")
    public ResponseEntity<ChatResponse> getChatRoom(@JwtAuth final AuthInfoDto authInfoDto,
                                                    @RequestParam("id") Long id){
        Member member = memberRepository.findByMemberId(authInfoDto.getMemberId()).orElseThrow(() -> new IllegalArgumentException("해당하는 회원이 없습니다."));

        return ResponseEntity.ok().body(new ChatResponse(
                chatService.getChatRoom(authInfoDto.getMemberId(), id),
                member.getMemberNickname()
        ));
    }

    @GetMapping("/api/chatList")
    public ResponseEntity<List<ChatRoomListResponse>> getChatRoomList(@JwtAuth final AuthInfoDto authInfoDto){
        List<ChatRoomListResponse> response = chatService.getChatRoomList(authInfoDto.getMemberId()).stream()
                .map(chatRoom -> {
                    Long userId = chatRoom.getUser1() == authInfoDto.getMemberId() ? chatRoom.getUser2() : chatRoom.getUser1();
                    return new ChatRoomListResponse(
                            chatRoom.getChatRoomId().toString(),
                            userId,
                            memberRepository.findByMemberId(userId).orElseThrow(() -> new IllegalArgumentException("해당하는 회원이 없습니다."))
                                    .getMemberNickname()
                    );
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok().body(response);
    }
}
