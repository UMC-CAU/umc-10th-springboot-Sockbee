package com.example.umc10th.domain.review.converter;

import com.example.umc10th.domain.review.dto.ReviewRequestDto;
import com.example.umc10th.domain.review.dto.ReviewResponseDto;
import com.example.umc10th.domain.review.entity.Review;
import com.example.umc10th.domain.review.enums.ReviewStatus;
import com.example.umc10th.domain.store.entity.Store;
import com.example.umc10th.domain.user.entity.User;

public class ReviewConverter {

    public static Review toReview(ReviewRequestDto.CreateReviewRequest req, User user, Store store) {
        return Review.builder()
                .user(user)
                .store(store)
                .star(req.star())
                .content(req.content())
                .imageUrl(req.imageUrl())
                .status(ReviewStatus.ACTIVE)
                .build();
    }

    public static ReviewResponseDto.CreateReviewResponse toCreateReviewResponse(Review review) {
        return ReviewResponseDto.CreateReviewResponse.builder()
                .reviewId(review.getId())
                .storeName(review.getStore().getName())
                .star(review.getStar())
                .createdAt(review.getCreatedAt())
                .build();
    }

    /**
     * 내 리뷰 아이템 — imageUrl(사진) 의도적으로 제외.
     */
    public static ReviewResponseDto.MyReviewItem toMyReviewItem(Review review) {
        return ReviewResponseDto.MyReviewItem.builder()
                .reviewId(review.getId())
                .storeName(review.getStore().getName())
                .star(review.getStar())
                .content(review.getContent())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
