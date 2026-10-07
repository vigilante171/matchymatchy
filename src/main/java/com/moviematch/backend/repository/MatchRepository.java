package com.moviematch.backend.repository;

import com.moviematch.backend.model.Match;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MatchRepository extends MongoRepository<Match, String> {

    Optional<Match> findByUser1IdAndUser2IdAndMovieId(
            String user1Id,
            String user2Id,
            String movieId
    );

    List<Match> findByUser1IdOrUser2Id(
            String user1Id,
            String user2Id
    );
}