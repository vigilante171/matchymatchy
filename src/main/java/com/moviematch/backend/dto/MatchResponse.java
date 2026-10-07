package com.moviematch.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MatchResponse {

    private String id;

    private String matchedUserId;

    private String movieId;

    private LocalDateTime createdAt;
}