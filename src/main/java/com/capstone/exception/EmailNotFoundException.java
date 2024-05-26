package com.capstone.exception;

public class EmailNotFoundException extends RuntimeException {
    public EmailNotFoundException() {
        super("email not found.");
    }
}
