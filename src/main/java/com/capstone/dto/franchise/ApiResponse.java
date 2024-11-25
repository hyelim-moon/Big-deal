package com.capstone.dto.franchise;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ApiResponse<T> {
    private final T response;
    private final HttpStatus httpStatus;
    private final String message;

    private ApiResponse(T response, HttpStatus httpStatus, String message) {
        this.response = response;
        this.httpStatus = httpStatus;
        this.message = message;
    }

    public static <T> ApiResponse<T> success(Object code, HttpStatus httpStatus, String message, T response) {
        return new ApiResponse<>(response, httpStatus, message);
    }

    public static <T> ApiResponse<T> error(Object code, HttpStatus httpStatus, String message) {
        return new ApiResponse<>(null, httpStatus, message);
    }
}
