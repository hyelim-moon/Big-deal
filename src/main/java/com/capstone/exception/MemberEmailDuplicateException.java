package com.capstone.exception;

public class MemberEmailDuplicateException extends RuntimeException {
    public MemberEmailDuplicateException() {
        super("member email already exist.");
    }

}
