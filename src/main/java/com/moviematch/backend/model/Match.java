package com.moviematch.backend.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Setter
@Document(collection = "matches")
@CompoundIndex(
        name = "unique_match",
        def = "{'user1Id': 1, 'user2Id': 1, 'movieId': 1}",
        unique = true
)
public class Match {

    @Id
    private String id;

    private String user1Id;

    private String user2Id;

    private String movieId;

    private LocalDateTime createdAt;
}