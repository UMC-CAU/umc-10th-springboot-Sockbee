package com.example.umc10th.domain.auth.service;

import com.example.umc10th.domain.auth.client.KakaoOAuthClient;
import com.example.umc10th.domain.auth.converter.OAuthConverter;
import com.example.umc10th.domain.auth.dto.AuthRequestDto;
import com.example.umc10th.domain.auth.dto.AuthResponseDto;
import com.example.umc10th.domain.auth.dto.KakaoTokenResponse;
import com.example.umc10th.domain.auth.dto.KakaoUserResponse;
import com.example.umc10th.domain.auth.entity.OAuth;
import com.example.umc10th.domain.auth.enums.OAuthProvider;
import com.example.umc10th.domain.auth.exception.AuthErrorCode;
import com.example.umc10th.domain.auth.exception.AuthException;
import com.example.umc10th.domain.auth.repository.OAuthRepository;
import com.example.umc10th.domain.user.converter.UserConverter;
import com.example.umc10th.domain.user.entity.User;
import com.example.umc10th.domain.user.exception.UserErrorCode;
import com.example.umc10th.domain.user.exception.UserException;
import com.example.umc10th.domain.user.repository.UserRepository;
import com.example.umc10th.global.security.AuthMember;
import com.example.umc10th.global.security.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final OAuthRepository oAuthRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final KakaoOAuthClient kakaoOAuthClient;

    public AuthResponseDto.SignupResponse signup(AuthRequestDto.SignupRequest req) {
        // 이메일/닉네임 중복 검증
        if (userRepository.existsByEmail(req.email())) {
            throw new UserException(UserErrorCode.EMAIL_ALREADY_EXISTS);
        }
        if (userRepository.existsByNickname(req.nickname())) {
            throw new UserException(UserErrorCode.NICKNAME_ALREADY_EXISTS);
        }

        // 비밀번호 BCrypt 해싱 (솔트는 해시마다 자동 생성·내장)
        String encodedPassword = passwordEncoder.encode(req.password());

        // User 생성 & 저장
        User user = UserConverter.toUser(req, encodedPassword);
        User saved = userRepository.save(user);

        return UserConverter.toSignupResponse(saved);
    }

    /**
     * 이메일 + 비밀번호 검증 후 access token 발급.
     * 사용자 존재 여부와 비밀번호 불일치를 동일한 에러(INVALID_CREDENTIALS)로 묶어 사용자 열거 공격을 방지한다.
     */
    @Transactional(readOnly = true)
    public AuthResponseDto.LoginResponse login(AuthRequestDto.LoginRequest req) {
        User user = userRepository.findByEmail(req.email())
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(req.password(), user.getPassword())) {
            throw new AuthException(AuthErrorCode.INVALID_CREDENTIALS);
        }

        String accessToken = jwtUtil.createAccessToken(new AuthMember(user));
        return UserConverter.toLoginResponse(user, accessToken);
    }

    /**
     * 카카오 OAuth 로그인 — Authorization Code Grant.
     *
     * 1. code → 카카오 access token 교환
     * 2. access token → 카카오 유저 정보 조회
     * 3. provider+providerUserId로 기존 OAuth 레코드 조회 → 있으면 그 User
     * 4. 없으면 동일 이메일 User 자동 연결, 그것도 없으면 신규 User 생성
     * 5. 자체 JWT 발급
     */
    public AuthResponseDto.LoginResponse loginWithKakao(AuthRequestDto.KakaoLoginRequest req) {
        KakaoTokenResponse token = kakaoOAuthClient.exchangeCodeForToken(req.code());
        KakaoUserResponse kakaoUser = kakaoOAuthClient.fetchUserInfo(token.accessToken());

        if (kakaoUser.email() == null || kakaoUser.email().isBlank()) {
            throw new AuthException(AuthErrorCode.KAKAO_EMAIL_REQUIRED);
        }

        User user = oAuthRepository.findByProviderAndProviderUserId(OAuthProvider.KAKAO, kakaoUser.id())
                .map(OAuth::getUser)
                .orElseGet(() -> linkOrCreateKakaoUser(kakaoUser));

        String accessToken = jwtUtil.createAccessToken(new AuthMember(user));
        return UserConverter.toLoginResponse(user, accessToken);
    }

    /**
     * 같은 이메일의 자체 가입 User가 있으면 OAuth 레코드만 추가하여 자동 연결,
     * 없으면 신규 User + OAuth 레코드를 함께 생성한다.
     */
    private User linkOrCreateKakaoUser(KakaoUserResponse kakao) {
        User user = userRepository.findByEmail(kakao.email())
                .orElseGet(() -> {
                    String randomPassword = passwordEncoder.encode(UUID.randomUUID().toString());
                    String uniqueNickname = resolveUniqueNickname(kakao.nickname());
                    return userRepository.save(
                            OAuthConverter.toNewUser(kakao, randomPassword, uniqueNickname)
                    );
                });
        oAuthRepository.save(OAuthConverter.toOAuth(user, OAuthProvider.KAKAO, kakao.id()));
        return user;
    }

    /**
     * 닉네임 중복 회피 — 충돌 시 6글자 접미사 부여.
     */
    private String resolveUniqueNickname(String base) {
        String root = (base == null || base.isBlank()) ? "kakao_user" : base;
        String candidate = root;
        while (userRepository.existsByNickname(candidate)) {
            candidate = root + "_" + UUID.randomUUID().toString().substring(0, 6);
        }
        return candidate;
    }
}
