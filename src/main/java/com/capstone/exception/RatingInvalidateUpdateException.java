package com.capstone.exception;

public class RatingInvalidateUpdateException extends RuntimeException {
    public RatingInvalidateUpdateException(String message) {
        super(message);
    }
    public RatingInvalidateUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
