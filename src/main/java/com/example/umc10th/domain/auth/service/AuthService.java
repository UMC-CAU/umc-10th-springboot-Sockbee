package com.example.umc10th.domain.auth.service;

import com.example.umc10th.domain.auth.dto.AuthRequestDto;
import com.example.umc10th.domain.auth.dto.AuthResponseDto;
import com.example.umc10th.domain.auth.exception.AuthErrorCode;
import com.example.umc10th.domain.auth.exception.AuthException;
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

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

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
}
