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

    /**
     * 진행 중인 미션 목록 응답 — 오프셋 페이지네이션 메타데이터 포함.
     * (커서 기반 API들은 글로벌 Pagination<T> 사용 — 여긴 오프셋 전용)
     */
    @Getter
    @Builder
    public static class ChallengingMissionListResponse {
        private List<UserMissionItem> missions;
        private int currentPage;
        private int totalPages;
        private long totalElements;
        private boolean hasNext;
        private boolean hasPrevious;
    }
}
