package com.moviematch.backend.dto;

import com.moviematch.backend.model.SwipeDirection;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SwipeRequest {

    @NotBlank
    private String movieId;

    @NotNull
    private SwipeDirection direction;
}