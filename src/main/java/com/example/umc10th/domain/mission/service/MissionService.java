package com.example.umc10th.domain.mission.service;

import com.example.umc10th.domain.mission.converter.MissionConverter;
import com.example.umc10th.domain.mission.dto.MissionResponseDto;
import com.example.umc10th.domain.mission.entity.Mission;
import com.example.umc10th.domain.mission.repository.MissionRepository;
import com.example.umc10th.global.apiPayload.Pagination;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MissionService {

    private final MissionRepository missionRepository;

    public Pagination<MissionResponseDto.AvailableMissionItem> getAvailableMissions(
            Long dongId, String cursor, int size) {

        Long lastMissionId = (cursor == null || cursor.isBlank()) ? null : Long.parseLong(cursor);

        Slice<Mission> slice = missionRepository.findAvailableMissions(
                dongId, lastMissionId, PageRequest.of(0, size));

        Slice<MissionResponseDto.AvailableMissionItem> mapped =
                slice.map(MissionConverter::toAvailableMissionItem);

        // 다음 커서 = 마지막 mission의 id (단일 컬럼 커서)
        String nextCursor = slice.hasNext()
                ? String.valueOf(slice.getContent().getLast().getId())
                : null;

        return Pagination.of(mapped, nextCursor);
    }
}
