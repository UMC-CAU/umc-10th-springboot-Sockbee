package com.example.umc10th.domain.user.dto;

import com.example.umc10th.domain.mission.enums.MissionStatus;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class UserMissionRequestDto {

    public record UpdateMissionStatusRequest(
            @NotNull MissionStatus status
    ) {}

    /**
     * 진행 중인 미션 조회 요청 — 오프셋 페이지네이션.
     * page, size는 null 허용 (compact constructor에서 기본값 주입).
     */
    public record GetChallengingMissionsRequest(
            @NotNull Long userId,
            @Min(0) Integer page,
            @Min(1) Integer size
    ) {
        public GetChallengingMissionsRequest {
            if (page == null) page = 0;
            if (size == null) size = 10;
        }
    }
}
