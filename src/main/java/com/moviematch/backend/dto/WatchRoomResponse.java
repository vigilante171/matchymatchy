package com.moviematch.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WatchRoomResponse {

    private String id;

    private String matchId;

    private String movieId;

    private String hostUserId;

    private boolean active;

    private LocalDateTime createdAt;
}