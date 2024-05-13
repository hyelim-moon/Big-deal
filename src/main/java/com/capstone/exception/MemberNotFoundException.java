package com.capstone.exception;

public class MemberNotFoundException extends RuntimeException {
    public MemberNotFoundException() {
        super("not exist member uuid.");
    }
}
