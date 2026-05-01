package com.example.umc10th.domain.mission.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class MissionResponseDto {

    @Getter
    @Builder
    public static class AvailableMissionListResponse {
        private List<AvailableMissionItem> missions;
        private boolean hasNext;
        private Long lastMissionId;
    }

    @Getter
    @Builder
    public static class AvailableMissionItem {
        private Long missionId;
        private String storeName;
        private String content;
        private int completePoint;
    }
}
