package com.example.umc10th.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 카카오 /v2/user/me 응답.
 * 응답 예: { "id": 12345, "kakao_account": { "email": "x@y.com", "profile": { "nickname": "..." } } }
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoUserResponse(
        Long id,
        @JsonProperty("kakao_account") KakaoAccount kakaoAccount
) {
    public String email() {
        return kakaoAccount == null ? null : kakaoAccount.email();
    }

    public String nickname() {
        if (kakaoAccount == null || kakaoAccount.profile() == null) return null;
        return kakaoAccount.profile().nickname();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record KakaoAccount(
            String email,
            @JsonProperty("has_email") Boolean hasEmail,
            @JsonProperty("is_email_verified") Boolean isEmailVerified,
            Profile profile
    ) {}

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Profile(
            String nickname,
            @JsonProperty("profile_image_url") String profileImageUrl
    ) {}
}
