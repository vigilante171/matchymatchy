package com.moviematch.backend.dto;

import com.moviematch.backend.model.SwipeDirection;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SwipeResponse {

    private String id;
    private String movieId;
    private SwipeDirection direction;
    private LocalDateTime createdAt;

    // Match details
    private boolean match;
    private String matchedUserId;
}