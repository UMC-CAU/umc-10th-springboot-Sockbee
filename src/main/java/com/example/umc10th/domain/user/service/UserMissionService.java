package com.example.umc10th.domain.user.service;

import com.example.umc10th.domain.mission.enums.MissionStatus;
import com.example.umc10th.domain.mission.exception.MissionErrorCode;
import com.example.umc10th.domain.mission.exception.MissionException;
import com.example.umc10th.domain.user.converter.UserMissionConverter;
import com.example.umc10th.domain.user.dto.UserMissionRequestDto;
import com.example.umc10th.domain.user.dto.UserMissionResponseDto;
import com.example.umc10th.domain.user.entity.UserMission;
import com.example.umc10th.domain.user.repository.UserMissionRepository;
import com.example.umc10th.global.apiPayload.Pagination;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserMissionService {

    private final UserMissionRepository userMissionRepository;

    /**
     * 홈 미션 카운트 — 완료(COMPLETE) 미션 수 % 10 으로 응답.
     */
    public UserMissionResponseDto.MissionCountResponse getMissionCount(Long userId, Long dongId) {
        int total = userMissionRepository.countCompletedByUserAndDong(userId, dongId);
        return UserMissionConverter.toMissionCountResponse(total % 10);
    }

    /**
     * 내 미션 목록 — createdAt + id 복합 커서 페이징.
     * cursor 포맷: "{epochMilli}:{id}" (opaque)
     */
    public Pagination<UserMissionResponseDto.UserMissionItem> getMyMissions(
            Long userId, String status, String cursor, int size) {

        MissionStatus parsedStatus = parseStatus(status);

        Long lastId = null;
        LocalDateTime lastCreatedAt = null;
        if (cursor != null && !cursor.isBlank()) {
            String[] parts = cursor.split(":");
            if (parts.length != 2) {
                throw new MissionException(MissionErrorCode.INVALID_MISSION_STATUS);
            }
            lastCreatedAt = LocalDateTime.ofEpochSecond(
                    Long.parseLong(parts[0]) / 1000, 0, ZoneOffset.UTC);
            lastId = Long.parseLong(parts[1]);
        }

        Slice<UserMission> slice = userMissionRepository.findMyMissions(
                userId, parsedStatus, lastId, lastCreatedAt, PageRequest.of(0, size));

        Slice<UserMissionResponseDto.UserMissionItem> mapped =
                slice.map(UserMissionConverter::toUserMissionItem);

        String nextCursor = null;
        if (slice.hasNext()) {
            UserMission tail = slice.getContent().getLast();
            long epochMs = tail.getCreatedAt().toInstant(ZoneOffset.UTC).toEpochMilli();
            nextCursor = epochMs + ":" + tail.getId();
        }

        return Pagination.of(mapped, nextCursor);
    }

    /**
     * 미션 성공 처리 — 상태 COMPLETE 변경 + closedAt 기록 + 사용자 포인트 적립.
     */
    @Transactional
    public UserMissionResponseDto.UpdateMissionStatusResponse updateMissionStatus(
            Long userId, Long userMissionId, UserMissionRequestDto.UpdateMissionStatusRequest req) {

        UserMission userMission = userMissionRepository.findById(userMissionId)
                .orElseThrow(() -> new MissionException(MissionErrorCode.USER_MISSION_NOT_FOUND));

        if (!userMission.getUser().getId().equals(userId)) {
            throw new MissionException(MissionErrorCode.USER_MISSION_FORBIDDEN);
        }
        if (userMission.getStatus() == MissionStatus.COMPLETE) {
            throw new MissionException(MissionErrorCode.MISSION_ALREADY_COMPLETED);
        }
        if (req.status() != MissionStatus.COMPLETE) {
            throw new MissionException(MissionErrorCode.INVALID_MISSION_STATUS);
        }

        userMission.complete();
        userMission.getUser().addPoint(userMission.getMission().getCompletePoint());

        return UserMissionConverter.toUpdateMissionStatusResponse(userMission);
    }

    private MissionStatus parseStatus(String status) {
        try {
            return MissionStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new MissionException(MissionErrorCode.INVALID_MISSION_STATUS);
        }
    }

    /**
     * 진행 중(CHALLENGING)인 내 미션 조회 — 오프셋 페이지네이션 (Page<> 메타데이터 보존).
     * 커서 기반과 의도적으로 분리한 케이스.
     */
    public UserMissionResponseDto.ChallengingMissionListResponse getChallengingMissions(
            UserMissionRequestDto.GetChallengingMissionsRequest req) {

        Page<UserMission> page = userMissionRepository.findChallengingMissions(
                req.userId(),
                PageRequest.of(req.page(), req.size())
        );

        return UserMissionConverter.toChallengingMissionListResponse(page);
    }
}
