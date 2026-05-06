package com.example.umc10th.domain.review.dto;

import jakarta.validation.constraints.NotNull;

public class ReviewRequestDto {

    public record CreateReviewRequest(
            @NotNull Long storeId,
            Float star,
            String content,
            String imageUrl
    ) {}
}
