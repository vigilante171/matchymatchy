package com.moviematch.backend.service;

import com.moviematch.backend.dto.SwipeRequest;
import com.moviematch.backend.dto.SwipeResponse;
import com.moviematch.backend.exception.DuplicateSwipeException;
import com.moviematch.backend.model.Swipe;
import com.moviematch.backend.model.SwipeDirection;
import com.moviematch.backend.model.User;
import com.moviematch.backend.repository.SwipeRepository;
import com.moviematch.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class SwipeService {

    private final SwipeRepository swipeRepository;
    private final UserRepository userRepository;

    public SwipeService(
            SwipeRepository swipeRepository,
            UserRepository userRepository) {

        this.swipeRepository = swipeRepository;
        this.userRepository = userRepository;
    }

    public SwipeResponse createSwipe(
            String email,
            SwipeRequest request) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (swipeRepository
                .findByUserIdAndMovieId(user.getId(), request.getMovieId())
                .isPresent()) {

            throw new DuplicateSwipeException("You have already swiped on this movie");
        }

        Swipe swipe = new Swipe();
        swipe.setUserId(user.getId());
        swipe.setMovieId(request.getMovieId());
        swipe.setDirection(request.getDirection());
        swipe.setCreatedAt(LocalDateTime.now());

        Swipe savedSwipe = swipeRepository.save(swipe);

        SwipeResponse response = new SwipeResponse();
        response.setId(savedSwipe.getId());
        response.setMovieId(savedSwipe.getMovieId());
        response.setDirection(savedSwipe.getDirection());
        response.setCreatedAt(savedSwipe.getCreatedAt());

        // Check for match if current action is LIKE
        if (request.getDirection() == SwipeDirection.LIKE) {
            Optional<Swipe> matchingSwipe = swipeRepository
                    .findFirstByMovieIdAndDirectionAndUserIdNot(
                            request.getMovieId(),
                            SwipeDirection.LIKE,
                            user.getId()
                    );

            if (matchingSwipe.isPresent()) {
                response.setMatch(true);
                response.setMatchedUserId(matchingSwipe.get().getUserId());
            } else {
                response.setMatch(false);
            }
        }

        return response;
    }
}