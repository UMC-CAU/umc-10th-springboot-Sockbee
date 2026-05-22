package com.example.umc10th.domain.review.service;

import com.example.umc10th.domain.review.converter.ReviewConverter;
import com.example.umc10th.domain.review.dto.ReviewRequestDto;
import com.example.umc10th.domain.review.dto.ReviewResponseDto;
import com.example.umc10th.domain.review.entity.Review;
import com.example.umc10th.domain.review.enums.ReviewSort;
import com.example.umc10th.domain.review.repository.ReviewRepository;
import com.example.umc10th.domain.store.entity.Store;
import com.example.umc10th.domain.store.exception.StoreErrorCode;
import com.example.umc10th.domain.store.exception.StoreException;
import com.example.umc10th.domain.store.repository.StoreRepository;
import com.example.umc10th.domain.user.entity.User;
import com.example.umc10th.domain.user.exception.UserErrorCode;
import com.example.umc10th.domain.user.exception.UserException;
import com.example.umc10th.domain.user.repository.UserRepository;
import com.example.umc10th.global.apiPayload.Pagination;
import com.example.umc10th.global.apiPayload.code.GeneralErrorCode;
import com.example.umc10th.global.exception.ProjectException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final StoreRepository storeRepository;
    private final UserRepository userRepository;

    public ReviewResponseDto.CreateReviewResponse createReview(
            Long userId, ReviewRequestDto.CreateReviewRequest req) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));

        Store store = storeRepository.findById(req.storeId())
                .orElseThrow(() -> new StoreException(StoreErrorCode.STORE_NOT_FOUND));

        Review saved = reviewRepository.save(ReviewConverter.toReview(req, user, store));
        return ReviewConverter.toCreateReviewResponse(saved);
    }

    /**
     * 내 리뷰 목록 조회 — 커서 페이지네이션 + 정렬 분기.
     * cursor 포맷:
     *  - sort=ID  : "{id}"
     *  - sort=STAR: "{star}:{id}"
     */
    @Transactional(readOnly = true)
    public Pagination<ReviewResponseDto.MyReviewItem> getMyReviews(
            Long userId, ReviewSort sort, String cursor, int size) {

        Slice<Review> slice;
        if (sort == ReviewSort.STAR) {
            slice = fetchByStar(userId, cursor, size);
        } else {
            slice = fetchById(userId, cursor, size);
        }

        Slice<ReviewResponseDto.MyReviewItem> mapped =
                slice.map(ReviewConverter::toMyReviewItem);

        String nextCursor = null;
        if (slice.hasNext()) {
            Review tail = slice.getContent().getLast();
            nextCursor = (sort == ReviewSort.STAR)
                    ? tail.getStar() + ":" + tail.getId()
                    : String.valueOf(tail.getId());
        }

        return Pagination.of(mapped, nextCursor);
    }

    private Slice<Review> fetchById(Long userId, String cursor, int size) {
        Long lastId = parseLongOrNull(cursor);
        return reviewRepository.findMyReviewsOrderById(userId, lastId, PageRequest.of(0, size));
    }

    private Slice<Review> fetchByStar(Long userId, String cursor, int size) {
        Float lastStar = null;
        Long lastId = null;
        if (cursor != null && !cursor.isBlank()) {
            String[] parts = cursor.split(":");
            if (parts.length != 2) {
                throw new ProjectException(GeneralErrorCode.BAD_REQUEST);
            }
            lastStar = Float.parseFloat(parts[0]);
            lastId = Long.parseLong(parts[1]);
        }
        return reviewRepository.findMyReviewsOrderByStar(userId, lastStar, lastId, PageRequest.of(0, size));
    }

    private Long parseLongOrNull(String cursor) {
        return (cursor == null || cursor.isBlank()) ? null : Long.parseLong(cursor);
    }
}
