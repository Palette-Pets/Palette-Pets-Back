package com.palette.palettepetsback.Chat.Controller.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomListResponse {
    private String roomId;
    private Long userId;
    private String nickname;
}
