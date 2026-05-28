package com.example.umc10th.domain.auth.dto;

import com.example.umc10th.domain.auth.enums.OAuthProvider;

/**
 * OAuth 임시 가입 토큰의 검증된 클레임.
 * type=signup 토큰을 파싱한 결과만 이 객체로 반환된다.
 */
public record SignupTokenClaims(
        OAuthProvider provider,
        Long providerUserId,
        String email,
        String nickname
) {}
