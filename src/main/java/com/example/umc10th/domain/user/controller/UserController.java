package com.example.umc10th.domain.user.controller;

import com.example.umc10th.domain.review.dto.ReviewRequestDto;
import com.example.umc10th.domain.review.dto.ReviewResponseDto;
import com.example.umc10th.domain.review.enums.ReviewSort;
import com.example.umc10th.domain.review.service.ReviewService;
import com.example.umc10th.domain.user.dto.UserMissionRequestDto;
import com.example.umc10th.domain.user.dto.UserMissionResponseDto;
import com.example.umc10th.domain.user.service.UserMissionService;
import com.example.umc10th.global.apiPayload.ApiResponse;
import com.example.umc10th.global.apiPayload.Pagination;
import com.example.umc10th.global.apiPayload.code.GeneralSuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
public class UserController {

    private final UserMissionService userMissionService;
    private final ReviewService reviewService;

    @GetMapping("/missions/count")
    public ApiResponse<UserMissionResponseDto.MissionCountResponse> getMissionCount(
            @RequestParam Long userId,
            @RequestParam Long dongId
    ) {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK,
                userMissionService.getMissionCount(userId, dongId));
    }

    @GetMapping("/missions")
    public ApiResponse<Pagination<UserMissionResponseDto.UserMissionItem>> getMyMissions(
            @RequestParam Long userId,
            @RequestParam String status,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK,
                userMissionService.getMyMissions(userId, status, cursor, size));
    }

    @PatchMapping("/missions/{userMissionId}")
    public ApiResponse<UserMissionResponseDto.UpdateMissionStatusResponse> updateMissionStatus(
            @RequestParam Long userId,
            @PathVariable Long userMissionId,
            @Valid @RequestBody UserMissionRequestDto.UpdateMissionStatusRequest request
    ) {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK,
                userMissionService.updateMissionStatus(userId, userMissionId, request));
    }

    @PostMapping("/reviews")
    public ApiResponse<ReviewResponseDto.CreateReviewResponse> createReview(
            @RequestParam Long userId,
            @Valid @RequestBody ReviewRequestDto.CreateReviewRequest request
    ) {
        return ApiResponse.onSuccess(GeneralSuccessCode.CREATED,
                reviewService.createReview(userId, request));
    }

    @GetMapping("/reviews")
    public ApiResponse<Pagination<ReviewResponseDto.MyReviewItem>> getMyReviews(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "ID") ReviewSort sort,
            @RequestParam(required = false) String cursor,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResponse.onSuccess(GeneralSuccessCode.OK,
                reviewService.getMyReviews(userId, sort, cursor, size));
    }
}
