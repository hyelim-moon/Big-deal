package com.capstone.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorCodeResponse {
    //private Integer code;
    private String message;
    public static ErrorCodeResponse of(/*Integer code,*/ String message) {
        return new ErrorCodeResponse(/*code,*/ message);
    }
}
