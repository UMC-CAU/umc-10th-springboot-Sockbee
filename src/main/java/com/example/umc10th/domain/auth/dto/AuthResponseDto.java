package com.example.umc10th.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class AuthResponseDto {

    @Getter
    @Builder
    public static class SignupResponse {
        private Long userId;
        private String nickname;
        private String email;
        private LocalDateTime createdAt;
    }
}
