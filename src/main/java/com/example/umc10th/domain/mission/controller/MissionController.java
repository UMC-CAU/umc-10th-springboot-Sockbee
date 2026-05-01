package com.example.umc10th.domain.mission.controller;

import com.example.umc10th.domain.mission.dto.MissionResponseDto;
import com.example.umc10th.global.apiPayload.ApiResponse;
import com.example.umc10th.global.apiPayload.code.GeneralSuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/missions")
@RequiredArgsConstructor
public class MissionController {

    @GetMapping
    public ApiResponse<MissionResponseDto.AvailableMissionListResponse> getAvailableMissions(
            @RequestParam Long dongId,
            @RequestParam(required = false) Long lastMissionId,
            @RequestParam(defaultValue = "10") int size
    ) {
        // TODO: service 구현 후 연결
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, null);
    }
}
