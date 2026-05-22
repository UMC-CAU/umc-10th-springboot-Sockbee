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

    /**
     * 내 리뷰 목록 개별 아이템. imageUrl(사진) 의도적 제외.
     */
    @Getter
    @Builder
    public static class MyReviewItem {
        private Long reviewId;
        private String storeName;
        private Float star;
        private String content;
        private LocalDateTime createdAt;
    }
}
