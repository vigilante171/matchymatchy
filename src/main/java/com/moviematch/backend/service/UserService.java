package com.moviematch.backend.service;

import com.moviematch.backend.dto.UserRequest;
import com.moviematch.backend.dto.UserResponse;
import com.moviematch.backend.exception.EmailAlreadyExistsException;
import com.moviematch.backend.model.User;
import com.moviematch.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    // 1. Declare the repository instance variable
    private final UserRepository userRepository;

    // 2. Inject it through the constructor
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponse createUser(UserRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException("Email is already registered");
        }

        // Map DTO to Entity and Save
        User user = new User();
        user.setEmail(request.getEmail());
        // set other properties...

        User savedUser = userRepository.save(user);

        // Map Entity to Response DTO
        UserResponse response = new UserResponse();
        response.setId(savedUser.getId());
        response.setEmail(savedUser.getEmail());

        return response;
    }
}