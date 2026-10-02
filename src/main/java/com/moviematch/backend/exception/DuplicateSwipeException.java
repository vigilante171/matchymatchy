package com.moviematch.backend.exception;

public class DuplicateSwipeException extends RuntimeException {

    public DuplicateSwipeException(String message) {
        super(message);
    }
}