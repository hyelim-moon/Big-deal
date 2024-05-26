package com.capstone.exception;

public class EmailInvalidateCodeException extends RuntimeException {
    public EmailInvalidateCodeException() {
        super("code wrong");
    }
}
