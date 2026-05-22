package com.example.umc10th.domain.mission.dto;

import lombok.Builder;
import lombok.Getter;

public class MissionResponseDto {

    @Getter
    @Builder
    public static class AvailableMissionItem {
        private Long missionId;
        private String storeName;
        private String content;
        private int completePoint;
    }
}
