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

    @Getter
    @Builder
    public static class LoginResponse {
        private String accessToken;
        private Long userId;
        private String nickname;
    }

    /**
     * 카카오 OAuth 1단계 응답.
     * - status="LOGGED_IN": 등록된 사용자 → accessToken/userId 발급
     * - status="NEEDS_SIGNUP": 미등록 사용자 → signupToken 발급 (프론트가 추가 정보 수집 후 /oauth/kakao/signup 호출)
     */
    @Getter
    @Builder
    public static class KakaoLoginResult {
        private String status;          // "LOGGED_IN" | "NEEDS_SIGNUP"
        // LOGGED_IN 시
        private String accessToken;
        private Long userId;
        // NEEDS_SIGNUP 시
        private String signupToken;
        // 공통 메타데이터 (카카오 또는 기존 User에서 추출)
        private String email;
        private String nickname;
    }
}
