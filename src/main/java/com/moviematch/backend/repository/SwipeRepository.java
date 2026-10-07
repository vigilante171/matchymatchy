package com.moviematch.backend.repository;

import com.moviematch.backend.model.Swipe;
import com.moviematch.backend.model.SwipeDirection;
import org.springframework.data.mongodb.repository.MongoRepository;


import java.util.Optional;

public interface SwipeRepository extends MongoRepository<Swipe, String> {

    Optional<Swipe> findByUserIdAndMovieId(String userId, String movieId);

    // Finds another user's LIKE swipe for the same movie
    Optional<Swipe> findFirstByMovieIdAndDirectionAndUserIdNot(
            String movieId,
            SwipeDirection direction,
            String userId
    );
}