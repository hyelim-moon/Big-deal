package com.capstone.exception;

public class MemberUsernameDuplicateException extends RuntimeException {
    public MemberUsernameDuplicateException() {
        super("username duplicate.");
    }
    public MemberUsernameDuplicateException(String message) {
        super(message);
    }
}
