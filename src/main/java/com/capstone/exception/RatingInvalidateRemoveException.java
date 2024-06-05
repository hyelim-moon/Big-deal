package com.capstone.exception;

public class RatingInvalidateRemoveException extends RuntimeException {
    public RatingInvalidateRemoveException(String message) {
        super(message);
    }
    public RatingInvalidateRemoveException(String message, Throwable cause) {
        super(message, cause);
    }
}
