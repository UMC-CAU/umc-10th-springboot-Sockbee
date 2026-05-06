package com.example.umc10th.domain.user.dto;

import com.example.umc10th.domain.mission.enums.MissionStatus;
import jakarta.validation.constraints.NotNull;

public class UserMissionRequestDto {

    public record UpdateMissionStatusRequest(
            @NotNull MissionStatus status
    ) {}
}
