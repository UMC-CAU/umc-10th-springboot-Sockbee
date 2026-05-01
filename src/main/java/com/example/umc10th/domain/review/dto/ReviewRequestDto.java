package com.example.umc10th.domain.review.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public class ReviewRequestDto {

    @Getter
    public static class CreateReviewRequest {

        @NotNull
        private Long storeId;

        private Float star;

        private String content;

        private String imageUrl;
    }
}
