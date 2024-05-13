package com.capstone.exception;

public class MemberBadRequestException extends RuntimeException {
    public MemberBadRequestException() {
        super("bad request");
    }
}
