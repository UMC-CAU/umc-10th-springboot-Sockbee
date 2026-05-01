package com.example.umc10th.domain.user.dto;

import com.example.umc10th.domain.mission.enums.MissionStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public class UserMissionRequestDto {

    @Getter
    public static class UpdateMissionStatusRequest {

        @NotNull
        private MissionStatus status;
    }
}
