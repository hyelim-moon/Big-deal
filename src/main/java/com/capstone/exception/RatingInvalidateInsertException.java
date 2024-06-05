package com.capstone.exception;

public class RatingInvalidateInsertException extends RuntimeException {
    public RatingInvalidateInsertException() {
        super("rating insert invalidate.");
    }
    public RatingInvalidateInsertException(String message) {
        super(message);
    }
    public RatingInvalidateInsertException(String message, Throwable cause) {
        super(message, cause);
    }
}
