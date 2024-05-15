package com.capstone.exception;

public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException() {
        super("not exist member uuid.");
    }
    public MemberNotFoundException(String message) {
        super(message);
    }
    public MemberNotFoundException(Throwable e) {
        super(e);
    }
    public MemberNotFoundException(String message, Throwable e) {
        super(message, e);
    }
}
