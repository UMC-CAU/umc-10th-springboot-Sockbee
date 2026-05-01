package com.example.umc10th.domain.auth.controller;

import com.example.umc10th.domain.auth.dto.AuthRequestDto;
import com.example.umc10th.domain.auth.dto.AuthResponseDto;
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

    @PostMapping("/signup")
    public ApiResponse<AuthResponseDto.SignupResponse> signup(
            @Valid @RequestBody AuthRequestDto.SignupRequest request
    ) {
        // TODO: service 구현 후 연결
        return ApiResponse.onSuccess(GeneralSuccessCode.CREATED, null);
    }
}
