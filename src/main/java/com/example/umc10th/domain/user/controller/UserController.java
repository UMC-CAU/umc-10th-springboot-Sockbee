package com.example.umc10th.domain.user.controller;

import com.example.umc10th.domain.review.dto.ReviewRequestDto;
import com.example.umc10th.domain.review.dto.ReviewResponseDto;
import com.example.umc10th.domain.review.service.ReviewService;
import com.example.umc10th.domain.user.dto.UserMissionRequestDto;
import com.example.umc10th.domain.user.dto.UserMissionResponseDto;
import com.example.umc10th.domain.user.service.UserMissionService;
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

    private final UserMissionService userMissionService;
    private final ReviewService reviewService;

    @GetMapping("/missions/count")
    public ApiResponse<UserMissionResponseDto.MissionCountResponse> getMissionCount(
            @RequestParam Long userId,   // TODO: JWT 구현 후 @AuthenticationPrincipal로 교체
            @RequestParam Long dongId
    ) {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK,
                userMissionService.getMissionCount(userId, dongId));
    }

    @GetMapping("/missions")
    public ApiResponse<UserMissionResponseDto.MyMissionListResponse> getMyMissions(
            @RequestParam Long userId,   // TODO: JWT 구현 후 @AuthenticationPrincipal로 교체
            @RequestParam String status,
            @RequestParam(required = false) Long lastId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime lastCreatedAt,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK,
                userMissionService.getMyMissions(userId, status, lastId, lastCreatedAt, size));
    }

    @PatchMapping("/missions/{userMissionId}")
    public ApiResponse<UserMissionResponseDto.UpdateMissionStatusResponse> updateMissionStatus(
            @RequestParam Long userId,   // TODO: JWT 구현 후 @AuthenticationPrincipal로 교체
            @PathVariable Long userMissionId,
            @Valid @RequestBody UserMissionRequestDto.UpdateMissionStatusRequest request
    ) {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK,
                userMissionService.updateMissionStatus(userId, userMissionId, request));
    }

    @PostMapping("/reviews")
    public ApiResponse<ReviewResponseDto.CreateReviewResponse> createReview(
            @RequestParam Long userId,   // TODO: JWT 구현 후 @AuthenticationPrincipal로 교체
            @Valid @RequestBody ReviewRequestDto.CreateReviewRequest request
    ) {
        return ApiResponse.onSuccess(GeneralSuccessCode.CREATED,
                reviewService.createReview(userId, request));
    }
}
