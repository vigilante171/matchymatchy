package com.moviematch.backend.controller;

import com.moviematch.backend.dto.WatchRoomMessage;
import com.moviematch.backend.model.WatchRoom;
import com.moviematch.backend.service.WatchRoomService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.Set;

@Controller
public class WatchRoomWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final WatchRoomService watchRoomService;
    private final Validator validator;

    public WatchRoomWebSocketController(
            SimpMessagingTemplate messagingTemplate,
            WatchRoomService watchRoomService,
            Validator validator) {

        this.messagingTemplate = messagingTemplate;
        this.watchRoomService = watchRoomService;
        this.validator = validator;
    }

    @MessageMapping("/watch-room")
    public void handleMessage(
            WatchRoomMessage message,
            Authentication authentication) {

        // 1. Validate the incoming message.
        Set<ConstraintViolation<WatchRoomMessage>> violations =
                validator.validate(message);

        if (!violations.isEmpty()) {
            throw new IllegalArgumentException(
                    violations.iterator().next().getMessage()
            );
        }

        // 2. Update and save the playback state.
        WatchRoom updatedRoom = watchRoomService.updatePlayback(
                authentication.getName(),
                message
        );

        // 3. Broadcast the saved playback state.
        WatchRoomMessage updatedMessage = new WatchRoomMessage(
                updatedRoom.getId(),
                message.getAction(),
                updatedRoom.getCurrentPosition(),
                updatedRoom.isPlaying(),
                updatedRoom.getLastPlaybackUpdate()
        );

        messagingTemplate.convertAndSend(
                "/topic/watch-room/" + updatedRoom.getId(),
                updatedMessage
        );
    }
}