package com.capstone.entity;

public enum Role {
    USER, ADMIN;
    @Override
    public String toString() {
        return switch (this) {
            case USER -> "User";
            case ADMIN -> "Admin";
        };
    }
}
