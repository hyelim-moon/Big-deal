package com.capstone.service;

public class MemberNotFountException extends RuntimeException {
    public MemberNotFountException() {
        super("not exist member uuid.");
    }
}
