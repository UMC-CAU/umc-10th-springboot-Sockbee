package com.example.umc10th.domain.auth.exception;

import com.example.umc10th.global.apiPayload.code.BaseErrorCode;
import com.example.umc10th.global.exception.ProjectException;

public class AuthException extends ProjectException {
    public AuthException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
