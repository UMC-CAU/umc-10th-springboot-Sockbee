package com.example.umc10th.domain.review.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

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
     * 내 리뷰 목록의 개별 아이템.
     * 사진(imageUrl) 필드는 의도적으로 제외.
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

    /**
     * 내 리뷰 목록 응답 (커서 페이지네이션).
     * - sort=ID  : lastId만 사용, lastStar는 null
     * - sort=STAR: (lastStar, lastId) 복합 커서
     */
    @Getter
    @Builder
    public static class MyReviewListResponse {
        private List<MyReviewItem> reviews;
        private boolean hasNext;
        private Long lastId;
        private Float lastStar;
    }
}
