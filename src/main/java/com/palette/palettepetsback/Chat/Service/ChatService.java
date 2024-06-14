package com.palette.palettepetsback.Chat.Service;

import com.palette.palettepetsback.Chat.Entity.ChatRoom;
import com.palette.palettepetsback.Chat.Repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;

    @Transactional
    public String getChatRoom(Long my_id, Long receiver_id){
        String room_name = chatRepository.findChatRoomById(my_id, receiver_id).orElse(null);
        if (room_name == null) {
            System.out.println("room is NULL");
            return createChatRoom(my_id, receiver_id);
        }
        return room_name;
    }

    @Transactional
    public String createChatRoom(Long user1, Long user2){
        ChatRoom chatRoom = chatRepository.save(
                ChatRoom.builder()
                        .user1(user1)
                        .user2(user2)
                        .build()
        );
        return chatRoom.getChatRoomId().toString();
    }
}
