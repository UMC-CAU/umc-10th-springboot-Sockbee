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
}
