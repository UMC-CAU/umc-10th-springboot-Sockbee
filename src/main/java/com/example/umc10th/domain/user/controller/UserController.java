package com.example.umc10th.domain.user.controller;

import com.example.umc10th.domain.review.dto.ReviewRequestDto;
import com.example.umc10th.domain.review.dto.ReviewResponseDto;
import com.example.umc10th.domain.user.dto.UserMissionRequestDto;
import com.example.umc10th.domain.user.dto.UserMissionResponseDto;
import com.example.umc10th.global.apiPayload.ApiResponse;
import com.example.umc10th.global.apiPayload.code.GeneralSuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
public class UserController {

    @GetMapping("/missions/count")
    public ApiResponse<UserMissionResponseDto.MissionCountResponse> getMissionCount(
            @RequestParam Long dongId
    ) {
        // TODO: service 구현 후 연결
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, null);
    }

    @GetMapping("/missions")
    public ApiResponse<UserMissionResponseDto.MyMissionListResponse> getMyMissions(
            @RequestParam String status,
            @RequestParam(required = false) Long lastId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastCreatedAt,
            @RequestParam(defaultValue = "10") int size
    ) {
        // TODO: service 구현 후 연결
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, null);
    }

    @PatchMapping("/missions/{userMissionId}")
    public ApiResponse<UserMissionResponseDto.UpdateMissionStatusResponse> updateMissionStatus(
            @PathVariable Long userMissionId,
            @Valid @RequestBody UserMissionRequestDto.UpdateMissionStatusRequest request
    ) {
        // TODO: service 구현 후 연결
        return ApiResponse.onSuccess(GeneralSuccessCode.OK, null);
    }

    @PostMapping("/reviews")
    public ApiResponse<ReviewResponseDto.CreateReviewResponse> createReview(
            @Valid @RequestBody ReviewRequestDto.CreateReviewRequest request
    ) {
        // TODO: service 구현 후 연결
        return ApiResponse.onSuccess(GeneralSuccessCode.CREATED, null);
    }
}
