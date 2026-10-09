package com.moviematch.backend.service;

import com.moviematch.backend.dto.PlaybackStateResponse;
import com.moviematch.backend.dto.WatchRoomMessage;
import com.moviematch.backend.dto.WatchRoomResponse;
import com.moviematch.backend.model.Match;
import com.moviematch.backend.model.WatchRoom;
import com.moviematch.backend.repository.MatchRepository;
import com.moviematch.backend.repository.UserRepository;
import com.moviematch.backend.repository.WatchRoomRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class WatchRoomService {

    private final WatchRoomRepository watchRoomRepository;
    private final MatchRepository matchRepository;
    private final UserRepository userRepository;

    public WatchRoomService(
            WatchRoomRepository watchRoomRepository,
            MatchRepository matchRepository,
            UserRepository userRepository) {

        this.watchRoomRepository = watchRoomRepository;
        this.matchRepository = matchRepository;
        this.userRepository = userRepository;
    }

    public WatchRoomResponse createRoom(
            String userId,
            String matchId) {

        Match match = matchRepository.findById(matchId)
                .orElseThrow(() ->
                        new RuntimeException("Match not found"));

        if (!match.getUser1Id().equals(userId)
                && !match.getUser2Id().equals(userId)) {

            throw new RuntimeException(
                    "You are not part of this match"
            );
        }

        WatchRoom existingRoom =
                watchRoomRepository.findByMatchId(matchId)
                        .orElse(null);

        if (existingRoom != null) {
            return mapToResponse(existingRoom);
        }

        WatchRoom room = new WatchRoom();

        room.setMatchId(matchId);
        room.setMovieId(match.getMovieId());
        room.setHostUserId(userId);
        room.setActive(true);
        room.setCreatedAt(LocalDateTime.now());

        WatchRoom savedRoom =
                watchRoomRepository.save(room);

        return mapToResponse(savedRoom);
    }

    public WatchRoomResponse getRoom(
            String userId,
            String roomId) {

        WatchRoom room = watchRoomRepository
                .findByIdAndActiveTrue(roomId)
                .orElseThrow(() ->
                        new RuntimeException("Watch room not found"));

        Match match = matchRepository
                .findById(room.getMatchId())
                .orElseThrow(() ->
                        new RuntimeException("Match not found"));

        if (!match.getUser1Id().equals(userId)
                && !match.getUser2Id().equals(userId)) {

            throw new RuntimeException(
                    "You are not part of this match"
            );
        }

        return mapToResponse(room);
    }

    public void verifyRoomAccess(
            String email,
            String roomId) {

        String userId = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"))
                .getId();

        WatchRoom room = watchRoomRepository
                .findByIdAndActiveTrue(roomId)
                .orElseThrow(() ->
                        new RuntimeException("Watch room not found"));

        Match match = matchRepository
                .findById(room.getMatchId())
                .orElseThrow(() ->
                        new RuntimeException("Match not found"));

        if (!match.getUser1Id().equals(userId)
                && !match.getUser2Id().equals(userId)) {

            throw new RuntimeException(
                    "You are not part of this match"
            );
        }
    }

    private WatchRoomResponse mapToResponse(
            WatchRoom room) {

        return new WatchRoomResponse(
                room.getId(),
                room.getMatchId(),
                room.getMovieId(),
                room.getHostUserId(),
                room.isActive(),
                room.getCreatedAt()
        );
    }
    public WatchRoom updatePlayback(
            String email,
            WatchRoomMessage message) {

        // Verify that the user can access this room.
        verifyRoomAccess(email, message.getRoomId());

        // Find the active room.
        WatchRoom room = watchRoomRepository
                .findByIdAndActiveTrue(message.getRoomId())
                .orElseThrow(() ->
                        new RuntimeException("Watch room not found"));

        // Update the playback state based on the requested action.
        switch (message.getAction()) {

            case PLAY -> {
                room.setCurrentPosition(message.getPosition());
                room.setPlaying(true);
            }

            case PAUSE -> {
                room.setCurrentPosition(message.getPosition());
                room.setPlaying(false);
            }

            case SEEK -> room.setCurrentPosition(message.getPosition());
        }

        room.setLastPlaybackUpdate(LocalDateTime.now());

        // Persist the updated state in MongoDB.
        return watchRoomRepository.save(room);
    }
    public PlaybackStateResponse getPlaybackState(
            String email,
            String roomId) {

        // Verify that the user belongs to this watch room.
        verifyRoomAccess(email, roomId);

        // Retrieve the active room.
        WatchRoom room = watchRoomRepository
                .findByIdAndActiveTrue(roomId)
                .orElseThrow(() ->
                        new RuntimeException("Watch room not found"));

        // Return the current playback state.
        return new PlaybackStateResponse(
                room.getId(),
                room.getMovieId(),
                room.getCurrentPosition(),
                room.isPlaying(),
                room.getLastPlaybackUpdate()
        );
    }
}