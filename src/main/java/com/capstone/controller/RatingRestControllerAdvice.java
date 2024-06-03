package com.capstone.controller;

import com.capstone.dto.ErrorCodeResponse;
import com.capstone.exception.RatingDuplicateException;
import com.capstone.exception.RatingInvalidateRemoveException;
import com.capstone.exception.RatingInvalidateUpdateException;
import com.capstone.exception.RatingNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class RatingRestControllerAdvice {
    @ExceptionHandler(value = RatingNotFoundException.class)
    public ResponseEntity<ErrorCodeResponse> ratingNotFound(RatingNotFoundException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorCodeResponse.of("rating not found"));
    }
    @ExceptionHandler(value = RatingInvalidateUpdateException.class)
    public ResponseEntity<ErrorCodeResponse> ratingInvalidateUpdate(RatingInvalidateUpdateException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorCodeResponse.of("you do not have permission to update for this rating."));
    }
    @ExceptionHandler(value = RatingInvalidateRemoveException.class)
    public ResponseEntity<ErrorCodeResponse> ratingInvalidateRemove(RatingInvalidateRemoveException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorCodeResponse.of("you do not have permission to remove for this rating."));
    }
    @ExceptionHandler(value = RatingDuplicateException.class)
    public ResponseEntity<ErrorCodeResponse> ratingDuplicate(RatingDuplicateException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorCodeResponse.of("you do not have permission to register a rating."));
    }
}
