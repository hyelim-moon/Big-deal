package com.capstone.controller;

import com.capstone.service.franchise.FranchiseNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class FranchiseRestControllerAdvice {
    @ExceptionHandler(value = FranchiseNotFoundException.class)
    public ResponseEntity<String> notFound(FranchiseNotFoundException notFoundException) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundException.getMessage());
    }
}
