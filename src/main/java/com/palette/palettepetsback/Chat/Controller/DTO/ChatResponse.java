package com.palette.palettepetsback.Chat.Controller.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    private String roomId;
    private String nickname;
}
