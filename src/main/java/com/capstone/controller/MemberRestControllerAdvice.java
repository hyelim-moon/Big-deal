package com.capstone.controller;

import com.capstone.dto.ErrorCodeResponse;
import com.capstone.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class MemberRestControllerAdvice {
    @ExceptionHandler(value = MemberNotFoundException.class)
    public ResponseEntity<ErrorCodeResponse> memberNotFound(MemberNotFoundException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorCodeResponse.of("unauthorized"));
    }
    @ExceptionHandler(value = MemberPasswordNotEqualsException.class)
    public ResponseEntity<ErrorCodeResponse> memberPasswordNotEquals(MemberPasswordNotEqualsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorCodeResponse.of("unauthorized"));
    }
    @ExceptionHandler(value = MemberUsernameDuplicateException.class)
    public ResponseEntity<ErrorCodeResponse> memberUsernameDuplicate(MemberUsernameDuplicateException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorCodeResponse.of("can't accept this request."));
    }
    @ExceptionHandler(value = MemberEmailDuplicateException.class)
    public ResponseEntity<ErrorCodeResponse> memberEmailDuplicate(MemberEmailDuplicateException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorCodeResponse.of("can't accept this request."));
    }
    @ExceptionHandler(value = MemberBadRequestException.class)
    public ResponseEntity<ErrorCodeResponse> memberBadRequest(MemberBadRequestException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorCodeResponse.of(e.getMessage()));
    }
    @ExceptionHandler(value = BadCredentialsException.class)
    public ResponseEntity<ErrorCodeResponse> badCredentials() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorCodeResponse.of("bad request or server error."));
    }
}
