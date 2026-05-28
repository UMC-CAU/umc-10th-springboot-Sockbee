package com.example.umc10th.domain.auth.converter;

import com.example.umc10th.domain.auth.dto.KakaoUserResponse;
import com.example.umc10th.domain.auth.entity.OAuth;
import com.example.umc10th.domain.auth.enums.OAuthProvider;
import com.example.umc10th.domain.user.entity.User;
import com.example.umc10th.domain.user.enums.UserStatus;

public class OAuthConverter {

    /**
     * 카카오 응답을 신규 User 엔티티로 변환.
     * 자체 회원가입과 달리 birthDate/gender/주소 등은 알 수 없으므로 null로 둔다.
     * (User 엔티티 nullable 완화 전제)
     */
    public static User toNewUser(KakaoUserResponse kakao, String encodedRandomPassword, String uniqueNickname) {
        return User.builder()
                .email(kakao.email())
                .nickname(uniqueNickname)
                .name(kakao.nickname())   // 카카오 닉네임을 기본 이름으로 사용
                .password(encodedRandomPassword)
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
