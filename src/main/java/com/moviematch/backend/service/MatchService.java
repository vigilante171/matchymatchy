package com.moviematch.backend.service;

import com.moviematch.backend.dto.MatchResponse;
import com.moviematch.backend.model.Match;
import com.moviematch.backend.repository.MatchRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MatchService {

    private final MatchRepository matchRepository;

    public MatchService(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    @Transactional
    public Match createMatch(String currentUserId, String matchedUserId, String movieId) {
        // 1. Validation & Guard Clauses
        Assert.hasText(currentUserId, "Current user ID must not be empty");
        Assert.hasText(matchedUserId, "Matched user ID must not be empty");
        Assert.hasText(movieId, "Movie ID must not be empty");

        if (currentUserId.equals(matchedUserId)) {
            throw new IllegalArgumentException("A user cannot match with themselves");
        }

        // 2. Canonical Ordering (Ensures user1Id is always lexicographically smaller)
        boolean isCurrentFirst = currentUserId.compareTo(matchedUserId) < 0;
        String user1Id = isCurrentFirst ? currentUserId : matchedUserId;
        String user2Id = isCurrentFirst ? matchedUserId : currentUserId;

        // 3. Find existing or save new record (Handles race condition fallback)
        return matchRepository
                .findByUser1IdAndUser2IdAndMovieId(user1Id, user2Id, movieId)
                .orElseGet(() -> createAndSaveMatch(user1Id, user2Id, movieId));
    }

    private Match createAndSaveMatch(String user1Id, String user2Id, String movieId) {
        try {
            Match match = new Match();
            match.setUser1Id(user1Id);
            match.setUser2Id(user2Id);
            match.setMovieId(movieId);
            match.setCreatedAt(LocalDateTime.now());

            return matchRepository.save(match);
        } catch (DuplicateKeyException e) {
            // Fallback for race condition: another thread inserted the record right before save()
            return matchRepository
                    .findByUser1IdAndUser2IdAndMovieId(user1Id, user2Id, movieId)
                    .orElseThrow(() -> e);
        }
    }

    @Transactional(readOnly = true)
    public List<MatchResponse> getUserMatches(String userId) {
        if (userId == null || userId.isBlank()) {
            return List.of();
        }

        List<Match> matches = matchRepository.findByUser1IdOrUser2Id(userId, userId);

        return matches.stream()
                .map(match -> mapToMatchResponse(match, userId))
                .toList();
    }

    private MatchResponse mapToMatchResponse(Match match, String currentUserId) {
        String counterpartUserId = match.getUser1Id().equals(currentUserId)
                ? match.getUser2Id()
                : match.getUser1Id();

        return new MatchResponse(
                match.getId(),
                counterpartUserId,
                match.getMovieId(),
                match.getCreatedAt()
        );
    }
}