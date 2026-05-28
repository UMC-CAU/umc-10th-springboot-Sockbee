package com.example.umc10th.domain.auth.service;

import com.example.umc10th.domain.auth.client.KakaoOAuthClient;
import com.example.umc10th.domain.auth.converter.OAuthConverter;
import com.example.umc10th.domain.auth.dto.AuthRequestDto;
import com.example.umc10th.domain.auth.dto.AuthResponseDto;
import com.example.umc10th.domain.auth.dto.KakaoTokenResponse;
import com.example.umc10th.domain.auth.dto.KakaoUserResponse;
import com.example.umc10th.domain.auth.dto.SignupTokenClaims;
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

import java.util.Optional;
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
     * 카카오 OAuth 1단계 — code 교환 후 등록 여부에 따라 분기.
     *
     * 1. provider+providerUserId 매칭 → 즉시 로그인
     * 2. 동일 이메일의 자체 가입자 매칭 → OAuth 레코드 자동 연결 후 로그인
     * 3. 매칭 없음 → 미등록. signup token 발급하여 NEEDS_SIGNUP 응답
     *
     * 실제 신규 User 생성은 별도 호출 `completeKakaoSignup`에서 추가 정보와 함께 수행한다.
     */
    public AuthResponseDto.KakaoLoginResult loginWithKakao(AuthRequestDto.KakaoLoginRequest req) {
        KakaoTokenResponse token = kakaoOAuthClient.exchangeCodeForToken(req.code());
        KakaoUserResponse kakao = kakaoOAuthClient.fetchUserInfo(token.accessToken());

        if (kakao.email() == null || kakao.email().isBlank()) {
            throw new AuthException(AuthErrorCode.KAKAO_EMAIL_REQUIRED);
        }

        // 1) OAuth 매칭
        Optional<User> byOAuth = oAuthRepository
                .findByProviderAndProviderUserId(OAuthProvider.KAKAO, kakao.id())
                .map(OAuth::getUser);
        if (byOAuth.isPresent()) {
            return toLoggedIn(byOAuth.get());
        }

        // 2) 이메일 매칭 → 자동 연결
        Optional<User> byEmail = userRepository.findByEmail(kakao.email());
        if (byEmail.isPresent()) {
            oAuthRepository.save(OAuthConverter.toOAuth(byEmail.get(), OAuthProvider.KAKAO, kakao.id()));
            return toLoggedIn(byEmail.get());
        }

        // 3) 미등록 → signup token 발급
        String signupToken = jwtUtil.createSignupToken(
                OAuthProvider.KAKAO, kakao.id(), kakao.email(), kakao.nickname());
        return AuthResponseDto.KakaoLoginResult.builder()
                .status("NEEDS_SIGNUP")
                .signupToken(signupToken)
                .email(kakao.email())
                .nickname(kakao.nickname())
                .build();
    }

    /**
     * 카카오 OAuth 2단계 — 임시 토큰 + 사용자 입력 추가 정보로 가입 완료 + access token 발급.
     */
    public AuthResponseDto.LoginResponse completeKakaoSignup(AuthRequestDto.KakaoSignupRequest req) {
        SignupTokenClaims claims = jwtUtil.parseSignupToken(req.signupToken())
                .orElseThrow(() -> new AuthException(AuthErrorCode.INVALID_SIGNUP_TOKEN));

        // 토큰 발급 후 다른 흐름으로 가입된 경우 방어
        if (userRepository.existsByEmail(claims.email())) {
            throw new UserException(UserErrorCode.EMAIL_ALREADY_EXISTS);
        }
        if (userRepository.existsByNickname(req.nickname())) {
            throw new UserException(UserErrorCode.NICKNAME_ALREADY_EXISTS);
        }

        String randomPw = passwordEncoder.encode(UUID.randomUUID().toString());
        User user = userRepository.save(
                OAuthConverter.toUserFromKakaoSignup(req, claims.email(), randomPw));
        oAuthRepository.save(
                OAuthConverter.toOAuth(user, claims.provider(), claims.providerUserId()));

        String accessToken = jwtUtil.createAccessToken(new AuthMember(user));
        return UserConverter.toLoginResponse(user, accessToken);
    }

    private AuthResponseDto.KakaoLoginResult toLoggedIn(User user) {
        String accessToken = jwtUtil.createAccessToken(new AuthMember(user));
        return AuthResponseDto.KakaoLoginResult.builder()
                .status("LOGGED_IN")
                .accessToken(accessToken)
                .userId(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .build();
    }
}
