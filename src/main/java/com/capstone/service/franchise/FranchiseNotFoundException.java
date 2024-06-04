package com.capstone.service.franchise;

public class FranchiseNotFoundException extends RuntimeException {
    public FranchiseNotFoundException() {
        super("not exist franchise uuid.");
    }
    public FranchiseNotFoundException(String message) {
        super(message);
    }
    public FranchiseNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
