package com.capstone.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Duration;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SetEmailCode {
    private String email;
    private String code;
    private Duration time;
}
