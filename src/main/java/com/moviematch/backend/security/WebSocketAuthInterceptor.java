package com.moviematch.backend.security;

import com.moviematch.backend.service.JwtService;
import com.moviematch.backend.service.WatchRoomService;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtService jwtService;
    private final WatchRoomService watchRoomService;

    public WebSocketAuthInterceptor(
            JwtService jwtService,
            WatchRoomService watchRoomService) {

        this.jwtService = jwtService;
        this.watchRoomService = watchRoomService;
    }

    @Override
    public Message<?> preSend(
            Message<?> message,
            MessageChannel channel) {

        StompHeaderAccessor accessor =
                StompHeaderAccessor.wrap(message);

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            String authorization =
                    accessor.getFirstNativeHeader("Authorization");

            if (authorization == null
                    || !authorization.startsWith("Bearer ")) {

                throw new IllegalArgumentException(
                        "Missing Authorization header"
                );
            }

            String token = authorization.substring(7);

            if (!jwtService.isTokenValid(token)) {

                throw new IllegalArgumentException(
                        "Invalid JWT token"
                );
            }

            String email = jwtService.extractEmail(token);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            List.of(
                                    new SimpleGrantedAuthority("USER")
                            )
                    );

            accessor.setUser(authentication);
        }

        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {

            String destination = accessor.getDestination();

            if (destination == null
                    || !destination.startsWith(
                    "/topic/watch-room/")) {

                return message;
            }

            String roomId = destination.substring(
                    "/topic/watch-room/".length()
            );

            if (accessor.getUser() == null) {
                throw new IllegalArgumentException(
                        "Authentication required"
                );
            }

            String email = accessor.getUser().getName();

            watchRoomService.verifyRoomAccess(
                    email,
                    roomId
            );
        }

        return message;
    }
}