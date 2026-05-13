package com.example.umc10th.domain.mission.converter;

import com.example.umc10th.domain.mission.dto.MissionResponseDto;
import com.example.umc10th.domain.mission.entity.Mission;

import java.util.List;

public class MissionConverter {

    public static MissionResponseDto.AvailableMissionItem toAvailableMissionItem(Mission mission) {
        return MissionResponseDto.AvailableMissionItem.builder()
                .missionId(mission.getId())
                .storeName(mission.getStore().getName())
                .content(mission.getContent())
                .completePoint(mission.getCompletePoint())
                .build();
    }

    public static MissionResponseDto.AvailableMissionListResponse toAvailableMissionListResponse(
            List<Mission> missions, boolean hasNext, Long lastMissionId) {
        List<MissionResponseDto.AvailableMissionItem> items = missions.stream()
                .map(MissionConverter::toAvailableMissionItem)
                .toList();
        return MissionResponseDto.AvailableMissionListResponse.builder()
                .missions(items)
                .hasNext(hasNext)
                .lastMissionId(lastMissionId)
                .build();
    }
}
