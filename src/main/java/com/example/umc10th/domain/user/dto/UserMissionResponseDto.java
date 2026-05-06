package com.example.umc10th.domain.user.dto;

import com.example.umc10th.domain.mission.enums.MissionStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class UserMissionResponseDto {

    @Getter
    @Builder
    public static class MissionCountResponse {
        private int missionCount;
    }

    @Getter
    @Builder
    public static class MyMissionListResponse {
        private List<UserMissionItem> missions;
        private boolean hasNext;
        private Long lastId;
        private LocalDateTime lastCreatedAt;
    }

    @Getter
    @Builder
    public static class UserMissionItem {
        private Long userMissionId;
        private String storeName;
        private String content;
        private int completePoint;
        private MissionStatus status;
        private LocalDateTime startedAt;
    }

    @Getter
    @Builder
    public static class UpdateMissionStatusResponse {
        private Long userMissionId;
        private MissionStatus status;
        private int earnedPoint;
        private LocalDateTime closedAt;
    }
}
