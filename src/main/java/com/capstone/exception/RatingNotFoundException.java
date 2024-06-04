package com.capstone.exception;

public class RatingNotFoundException extends RuntimeException {
    public RatingNotFoundException() {
        super("rating not found.");
    }
    public RatingNotFoundException(String message) {
        super(message);
    }
    public RatingNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
