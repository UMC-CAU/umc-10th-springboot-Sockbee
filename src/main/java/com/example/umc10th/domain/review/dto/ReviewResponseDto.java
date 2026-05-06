package com.example.umc10th.domain.review.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class ReviewResponseDto {

    @Getter
    @Builder
    public static class CreateReviewResponse {
        private Long reviewId;
        private String storeName;
        private Float star;
        private LocalDateTime createdAt;
    }
}
