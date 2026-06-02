package com.example.umc10th.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 카카오 OAuth 토큰 교환 응답.
 * 본 프로젝트에서는 access_token만 사용하지만, 안정성을 위해 알려진 필드를 모두 매핑.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record KakaoTokenResponse(
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("refresh_token") String refreshToken,
        @JsonProperty("expires_in") Integer expiresIn,
        @JsonProperty("refresh_token_expires_in") Integer refreshTokenExpiresIn,
        @JsonProperty("scope") String scope
) {}
