package com.example.umc10th.domain.user.dto;

import com.example.umc10th.domain.mission.enums.MissionStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class UserMissionRequestDto {

    public record UpdateMissionStatusRequest(
            @NotNull MissionStatus status
    ) {}

    /**
     * 진행 중인 미션 조회 요청 — 오프셋 페이지네이션.
     * - userId: 필수, 양수만 허용
     * - page: 0 이상 (생략 시 0)
     * - size: 1 ~ 100 (생략 시 10)
     */
    public record GetChallengingMissionsRequest(
            @NotNull(message = "userId는 필수입니다.")
            @Positive(message = "userId는 양수여야 합니다.")
            Long userId,

            @Min(value = 0, message = "page는 0 이상이어야 합니다.")
            Integer page,

            @Min(value = 1, message = "size는 1 이상이어야 합니다.")
            @Max(value = 100, message = "size는 100 이하여야 합니다.")
            Integer size
    ) {
        public GetChallengingMissionsRequest {
            if (page == null) page = 0;
            if (size == null) size = 10;
        }
    }
}
