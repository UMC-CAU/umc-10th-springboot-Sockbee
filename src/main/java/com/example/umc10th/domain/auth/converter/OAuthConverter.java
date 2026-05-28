package com.example.umc10th.domain.auth.converter;

import com.example.umc10th.domain.auth.dto.AuthRequestDto;
import com.example.umc10th.domain.auth.entity.OAuth;
import com.example.umc10th.domain.auth.enums.OAuthProvider;
import com.example.umc10th.domain.user.entity.User;
import com.example.umc10th.domain.user.enums.UserStatus;

public class OAuthConverter {

    /**
     * 카카오 임시 가입 토큰 + 사용자가 입력한 추가 정보로 신규 User 엔티티 생성.
     * email은 임시 토큰의 claim에서 추출 (카카오 검증된 값) — 클라이언트 위변조 차단.
     */
    public static User toUserFromKakaoSignup(
            AuthRequestDto.KakaoSignupRequest req,
            String email,
            String encodedRandomPassword
    ) {
        return User.builder()
                .email(email)
                .nickname(req.nickname())
                .name(req.userName())
                .password(encodedRandomPassword)
                .gender(req.gender())
                .birthDate(req.birthDate())
                .addressMain(req.addressMain())
                .addressDetail(req.addressDetail())
                .zipCode(req.zipCode())
                .status(UserStatus.ACTIVE)
                .missionCount(0)
                .point(0)
                .build();
    }

    public static OAuth toOAuth(User user, OAuthProvider provider, Long providerUserId) {
        return OAuth.builder()
                .user(user)
                .provider(provider)
                .providerUserId(providerUserId)
                .build();
    }
}
