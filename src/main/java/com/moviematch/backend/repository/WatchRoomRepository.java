package com.moviematch.backend.repository;

import com.moviematch.backend.model.WatchRoom;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface WatchRoomRepository
        extends MongoRepository<WatchRoom, String> {

    Optional<WatchRoom> findByMatchId(String matchId);

    Optional<WatchRoom> findByIdAndActiveTrue(String id);
    
}