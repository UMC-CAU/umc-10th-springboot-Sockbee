package com.example.umc10th.domain.auth.exception;

import com.example.umc10th.global.apiPayload.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements BaseErrorCode {

    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH401_1", "이메일 또는 비밀번호가 올바르지 않습니다."),
    INVALID_SIGNUP_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH401_2", "회원가입 토큰이 유효하지 않거나 만료되었습니다."),
    KAKAO_EMAIL_REQUIRED(HttpStatus.BAD_REQUEST, "AUTH400_1", "이메일 제공 동의가 필요합니다."),
    KAKAO_TOKEN_FETCH_FAILED(HttpStatus.BAD_GATEWAY, "AUTH502_1", "카카오 토큰 발급에 실패했습니다."),
    KAKAO_USER_INFO_FETCH_FAILED(HttpStatus.BAD_GATEWAY, "AUTH502_2", "카카오 사용자 정보 조회에 실패했습니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
