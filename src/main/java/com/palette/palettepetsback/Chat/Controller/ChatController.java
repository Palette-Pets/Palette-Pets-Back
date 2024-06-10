package com.palette.palettepetsback.Chat.Controller;

import com.palette.palettepetsback.Chat.Service.ChatService;
import com.palette.palettepetsback.config.jwt.AuthInfoDto;
import com.palette.palettepetsback.config.jwt.jwtAnnotation.JwtAuth;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @GetMapping("/api/chat")
    public String getChatRoom(@JwtAuth final AuthInfoDto authInfoDto,
                              @RequestParam("id") Long id){
        System.out.println("/api/chat :: authInfoDto.getMemberId() = " + authInfoDto.getMemberId());
        return chatService.getChatRoom(authInfoDto.getMemberId(), id);
    }
}
