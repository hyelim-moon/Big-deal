package com.capstone.exception;

public class MemberPasswordNotEqualsException extends RuntimeException {
    public MemberPasswordNotEqualsException() {
        super("password different.");
    }
}
