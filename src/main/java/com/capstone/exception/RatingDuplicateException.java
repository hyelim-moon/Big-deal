package com.capstone.exception;

public class RatingDuplicateException extends RuntimeException {
    public RatingDuplicateException(String message) {
        super(message);
    }
    public RatingDuplicateException(String message, Throwable cause) {
        super(message, cause);
    }
}
