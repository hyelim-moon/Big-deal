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
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorCodeResponse.of("unauthorized"));
    }
    @ExceptionHandler(value = MemberPasswordNotEqualsException.class)
    public ResponseEntity<ErrorCodeResponse> memberPasswordNotEquals(MemberPasswordNotEqualsException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorCodeResponse.of("unauthorized"));
    }
    @ExceptionHandler(value = MemberUsernameDuplicateException.class)
    public ResponseEntity<ErrorCodeResponse> memberUsernameDuplicate(MemberUsernameDuplicateException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorCodeResponse.of("can't accept this request."));
    }
    @ExceptionHandler(value = MemberEmailDuplicateException.class)
    public ResponseEntity<ErrorCodeResponse> memberEmailDuplicate(MemberEmailDuplicateException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorCodeResponse.of("can't accept this request."));
    }
    @ExceptionHandler(value = MemberBadRequestException.class)
    public ResponseEntity<ErrorCodeResponse> memberBadRequest(MemberBadRequestException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorCodeResponse.of(e.getMessage()));
    }
    @ExceptionHandler(value = MemberInvalidateLoginException.class)
    public ResponseEntity<ErrorCodeResponse> memberInvalidateLogin(MemberInvalidateLoginException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorCodeResponse.of("unauthorized"));
    }
    @ExceptionHandler(value = BadCredentialsException.class)
    public ResponseEntity<ErrorCodeResponse> badCredentials(BadCredentialsException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorCodeResponse.of("bad request or server error."));
    }
    @ExceptionHandler(value = EmailInvalidateCodeException.class)
    public ResponseEntity<ErrorCodeResponse> emailInvalidateCode(EmailInvalidateCodeException e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ErrorCodeResponse.of("unauthorized"));
    }
}
