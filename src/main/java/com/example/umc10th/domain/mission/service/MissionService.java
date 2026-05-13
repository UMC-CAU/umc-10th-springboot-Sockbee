package com.example.umc10th.domain.mission.service;

import com.example.umc10th.domain.mission.converter.MissionConverter;
import com.example.umc10th.domain.mission.dto.MissionResponseDto;
import com.example.umc10th.domain.mission.entity.Mission;
import com.example.umc10th.domain.mission.repository.MissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MissionService {

    private final MissionRepository missionRepository;

    public MissionResponseDto.AvailableMissionListResponse getAvailableMissions(
            Long dongId, Long lastMissionId, int size) {

        // size + 1로 조회해서 hasNext 판단
        List<Mission> rows = missionRepository.findAvailableMissions(
                dongId, lastMissionId, PageRequest.of(0, size + 1));

        boolean hasNext = rows.size() > size;
        List<Mission> page = hasNext ? rows.subList(0, size) : rows;
        Long nextCursor = page.isEmpty() ? null : page.get(page.size() - 1).getId();

        return MissionConverter.toAvailableMissionListResponse(page, hasNext, nextCursor);
    }
}
