package com.moviematch.backend.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "swipes")
public class Swipe {

    @Id
    private String id;

    private String userId;

    private String movieId;

    private SwipeDirection direction;

    private LocalDateTime createdAt;
}

