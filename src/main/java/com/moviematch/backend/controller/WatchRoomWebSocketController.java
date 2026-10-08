package com.moviematch.backend.controller;

import com.moviematch.backend.dto.WatchRoomMessage;
import com.moviematch.backend.service.WatchRoomService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Controller
public class WatchRoomWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final WatchRoomService watchRoomService;

    public WatchRoomWebSocketController(
            SimpMessagingTemplate messagingTemplate,
            WatchRoomService watchRoomService) {

        this.messagingTemplate = messagingTemplate;
        this.watchRoomService = watchRoomService;
    }

    @MessageMapping("/watch-room")
    public void handleMessage(
            WatchRoomMessage message,
            Authentication authentication) {

        watchRoomService.verifyRoomAccess(
                authentication.getName(),
                message.getRoomId()
        );

        messagingTemplate.convertAndSend(
                "/topic/watch-room/" + message.getRoomId(),
                message
        );
    }
}