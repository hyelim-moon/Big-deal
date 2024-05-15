package com.capstone.service.franchise;

public class FranchiseNotFoundException extends RuntimeException {
    public FranchiseNotFoundException() {
        super("not exist franchise uuid.");
    }
}
