package com.example.umc10th.domain.auth.exception;

import com.example.umc10th.global.apiPayload.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum AuthErrorCode implements BaseErrorCode {

    INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "AUTH401_1", "이메일 또는 비밀번호가 올바르지 않습니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
