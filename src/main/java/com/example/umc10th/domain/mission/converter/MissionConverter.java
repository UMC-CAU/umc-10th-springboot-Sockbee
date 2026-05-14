package com.example.umc10th.domain.mission.converter;

import com.example.umc10th.domain.mission.dto.MissionResponseDto;
import com.example.umc10th.domain.mission.entity.Mission;

public class MissionConverter {

    public static MissionResponseDto.AvailableMissionItem toAvailableMissionItem(Mission mission) {
        return MissionResponseDto.AvailableMissionItem.builder()
                .missionId(mission.getId())
                .storeName(mission.getStore().getName())
                .content(mission.getContent())
                .completePoint(mission.getCompletePoint())
                .build();
    }
}
