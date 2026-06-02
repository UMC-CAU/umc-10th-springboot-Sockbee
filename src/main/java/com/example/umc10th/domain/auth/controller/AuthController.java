package com.example.umc10th.domain.auth.controller;

import com.example.umc10th.domain.auth.dto.AuthRequestDto;
import com.example.umc10th.domain.auth.dto.AuthResponseDto;
import com.example.umc10th.domain.auth.service.AuthService;
import com.example.umc10th.global.apiPayload.ApiResponse;
import com.example.umc10th.global.apiPayload.code.GeneralSuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ApiResponse<AuthResponseDto.SignupResponse> signup(
            @Valid @RequestBody AuthRequestDto.SignupRequest request
    ) {
        return ApiResponse.onSuccess(GeneralSuccessCode.CREATED, authService.signup(request));
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponseDto.LoginResponse> login(
            @Valid @RequestBody AuthRequestDto.LoginRequest request
    ) {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, authService.login(request));
    }

    // 카카오 OAuth 1단계 — 인가 code 검증 후 등록 사용자면 즉시 로그인, 미등록이면 signupToken 반환
    @PostMapping("/oauth/kakao")
    public ApiResponse<AuthResponseDto.KakaoLoginResult> kakaoLogin(
            @Valid @RequestBody AuthRequestDto.KakaoLoginRequest request
    ) {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, authService.loginWithKakao(request));
    }

    // 카카오 OAuth 2단계 — 임시 가입 토큰 + 추가 정보로 가입 완료 + access token 발급
    @PostMapping("/oauth/kakao/signup")
    public ApiResponse<AuthResponseDto.LoginResponse> kakaoSignup(
            @Valid @RequestBody AuthRequestDto.KakaoSignupRequest request
    ) {
        return ApiResponse.onSuccess(GeneralSuccessCode.CREATED, authService.completeKakaoSignup(request));
    }
}
