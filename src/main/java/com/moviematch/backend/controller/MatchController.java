package com.moviematch.backend.controller;

import com.moviematch.backend.dto.MatchResponse;
import com.moviematch.backend.model.User;
import com.moviematch.backend.repository.UserRepository;
import com.moviematch.backend.service.MatchService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/matches")
public class MatchController {

    private final MatchService matchService;
    private final UserRepository userRepository;

    public MatchController(
            MatchService matchService,
            UserRepository userRepository) {

        this.matchService = matchService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<MatchResponse> getMyMatches(
            Authentication authentication) {

        User user = userRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        return matchService.getUserMatches(user.getId());
    }
}