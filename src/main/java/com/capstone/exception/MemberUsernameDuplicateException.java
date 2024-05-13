package com.capstone.exception;

public class MemberUsernameDuplicateException extends RuntimeException {
    public MemberUsernameDuplicateException() {
        super("username duplicate.");
    }
}
