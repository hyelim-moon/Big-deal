package com.capstone.exception;

public class RatingNotFoundException extends RuntimeException {
    public RatingNotFoundException() {
        super("rating not found.");
    }
}
