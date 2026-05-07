package com.example.umc10th.domain.mission.exception;

import com.example.umc10th.global.apiPayload.code.BaseErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MissionErrorCode implements BaseErrorCode {

    USER_MISSION_NOT_FOUND(HttpStatus.NOT_FOUND, "MISSION404_1", "해당 유저 미션을 찾을 수 없습니다."),
    USER_MISSION_FORBIDDEN(HttpStatus.FORBIDDEN, "MISSION403_1", "본인의 미션이 아닙니다."),
    MISSION_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "MISSION400_1", "이미 완료된 미션입니다."),
    INVALID_MISSION_STATUS(HttpStatus.BAD_REQUEST, "MISSION400_2", "올바르지 않은 미션 상태값입니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
