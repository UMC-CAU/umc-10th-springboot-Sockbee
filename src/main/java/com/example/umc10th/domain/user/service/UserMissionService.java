package com.example.umc10th.domain.user.service;

import com.example.umc10th.domain.mission.enums.MissionStatus;
import com.example.umc10th.domain.mission.exception.MissionErrorCode;
import com.example.umc10th.domain.mission.exception.MissionException;
import com.example.umc10th.domain.user.converter.UserMissionConverter;
import com.example.umc10th.domain.user.dto.UserMissionRequestDto;
import com.example.umc10th.domain.user.dto.UserMissionResponseDto;
import com.example.umc10th.domain.user.entity.UserMission;
import com.example.umc10th.domain.user.repository.UserMissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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
     */
    public UserMissionResponseDto.MyMissionListResponse getMyMissions(
            Long userId, String status, Long lastId, LocalDateTime lastCreatedAt, int size) {

        MissionStatus parsedStatus = parseStatus(status);

        List<UserMission> rows = userMissionRepository.findMyMissions(
                userId, parsedStatus, lastId, lastCreatedAt, PageRequest.of(0, size + 1));

        boolean hasNext = rows.size() > size;
        List<UserMission> page = hasNext ? rows.subList(0, size) : rows;

        Long nextLastId = null;
        LocalDateTime nextLastCreatedAt = null;
        if (!page.isEmpty()) {
            UserMission tail = page.get(page.size() - 1);
            nextLastId = tail.getId();
            nextLastCreatedAt = tail.getCreatedAt();
        }

        return UserMissionConverter.toMyMissionListResponse(page, hasNext, nextLastId, nextLastCreatedAt);
    }

    /**
     * 미션 성공 처리 — 상태 COMPLETE 변경 + closedAt 기록 + 사용자 포인트 적립.
     */
    @Transactional
    public UserMissionResponseDto.UpdateMissionStatusResponse updateMissionStatus(
            Long userId, Long userMissionId, UserMissionRequestDto.UpdateMissionStatusRequest req) {

        UserMission userMission = userMissionRepository.findById(userMissionId)
                .orElseThrow(() -> new MissionException(MissionErrorCode.USER_MISSION_NOT_FOUND));

        // 본인 소유 검증
        if (!userMission.getUser().getId().equals(userId)) {
            throw new MissionException(MissionErrorCode.USER_MISSION_FORBIDDEN);
        }

        // 이미 완료된 미션 차단
        if (userMission.getStatus() == MissionStatus.COMPLETE) {
            throw new MissionException(MissionErrorCode.MISSION_ALREADY_COMPLETED);
        }

        // 요청은 COMPLETE만 허용 (현재 API 스펙 기준)
        if (req.status() != MissionStatus.COMPLETE) {
            throw new MissionException(MissionErrorCode.INVALID_MISSION_STATUS);
        }

        userMission.complete();
        userMission.getUser().addPoint(userMission.getMission().getCompletePoint());
        // dirty checking으로 자동 저장

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
     * 진행 중(CHALLENGING)인 내 미션 조회 — 오프셋 페이지네이션.
     * userId는 RequestBody의 DTO에서 받아 하드코딩 X.
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
