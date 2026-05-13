package com.example.umc10th.domain.user.converter;

import com.example.umc10th.domain.user.dto.UserMissionResponseDto;
import com.example.umc10th.domain.user.entity.UserMission;

import java.time.LocalDateTime;
import java.util.List;

public class UserMissionConverter {

    public static UserMissionResponseDto.MissionCountResponse toMissionCountResponse(int missionCount) {
        return UserMissionResponseDto.MissionCountResponse.builder()
                .missionCount(missionCount)
                .build();
    }

    public static UserMissionResponseDto.UserMissionItem toUserMissionItem(UserMission um) {
        return UserMissionResponseDto.UserMissionItem.builder()
                .userMissionId(um.getId())
                .storeName(um.getMission().getStore().getName())
                .content(um.getMission().getContent())
                .completePoint(um.getMission().getCompletePoint())
                .status(um.getStatus())
                .startedAt(um.getStartedAt())
                .build();
    }

    public static UserMissionResponseDto.MyMissionListResponse toMyMissionListResponse(
            List<UserMission> userMissions, boolean hasNext, Long lastId, LocalDateTime lastCreatedAt) {
        List<UserMissionResponseDto.UserMissionItem> items = userMissions.stream()
                .map(UserMissionConverter::toUserMissionItem)
                .toList();
        return UserMissionResponseDto.MyMissionListResponse.builder()
                .missions(items)
                .hasNext(hasNext)
                .lastId(lastId)
                .lastCreatedAt(lastCreatedAt)
                .build();
    }

    public static UserMissionResponseDto.UpdateMissionStatusResponse toUpdateMissionStatusResponse(UserMission um) {
        return UserMissionResponseDto.UpdateMissionStatusResponse.builder()
                .userMissionId(um.getId())
                .status(um.getStatus())
                .earnedPoint(um.getMission().getCompletePoint())
                .closedAt(um.getClosedAt())
                .build();
    }
}
