package com.example.umc10th.domain.user.controller;

import com.example.umc10th.domain.user.dto.UserMissionRequestDto;
import com.example.umc10th.domain.user.dto.UserMissionResponseDto;
import com.example.umc10th.domain.user.service.UserMissionService;
import com.example.umc10th.global.apiPayload.ApiResponse;
import com.example.umc10th.global.apiPayload.code.GeneralSuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * userId를 Request Body로 받는 미션 관련 엔드포인트.
 * (UserController의 /me 패턴과는 분리 — 하드코딩 없이 body에서 동적 식별)
 */
@RestController
@RequestMapping("/api/users/missions")
@RequiredArgsConstructor
public class UserMissionController {

    private final UserMissionService userMissionService;

    /**
     * 내가 진행 중(CHALLENGING)인 미션 조회 — 오프셋 페이지네이션.
     * Request Body: { userId, page (선택, 기본 0), size (선택, 기본 10) }
     */
    @PostMapping("/challenging")
    public ApiResponse<UserMissionResponseDto.ChallengingMissionListResponse> getChallengingMissions(
            @Valid @RequestBody UserMissionRequestDto.GetChallengingMissionsRequest request
    ) {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK,
                userMissionService.getChallengingMissions(request));
    }
}
