package com.capstone.exception;

public class RatingDuplicateRemoveException extends RuntimeException {
    public RatingDuplicateRemoveException() {
        super("rating already removed");
    }
    public RatingDuplicateRemoveException(String message) {
        super(message);
    }
    public RatingDuplicateRemoveException(String message, Throwable cause) {
        super(message, cause);
    }
}
