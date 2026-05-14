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
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
     * 내가 생성한 리뷰 목록 조회 (커서 페이지네이션).
     * - sort=ID  : id 내림차순, 단일 커서(lastId)
     * - sort=STAR: 별점 내림차순, 복합 커서(lastStar, lastId)
     * 사진(imageUrl)은 응답에서 제외.
     */
    @Transactional(readOnly = true)
    public ReviewResponseDto.MyReviewListResponse getMyReviews(
            Long userId, ReviewSort sort, Long lastId, Float lastStar, int size) {

        List<Review> rows = (sort == ReviewSort.STAR)
                ? reviewRepository.findMyReviewsOrderByStar(
                        userId, lastStar, lastId, PageRequest.of(0, size + 1))
                : reviewRepository.findMyReviewsOrderById(
                        userId, lastId, PageRequest.of(0, size + 1));

        boolean hasNext = rows.size() > size;
        List<Review> page = hasNext ? rows.subList(0, size) : rows;

        Long nextLastId = null;
        Float nextLastStar = null;
        if (!page.isEmpty()) {
            Review tail = page.get(page.size() - 1);
            nextLastId = tail.getId();
            // 별점 정렬일 때만 lastStar 응답에 포함
            if (sort == ReviewSort.STAR) {
                nextLastStar = tail.getStar();
            }
        }

        return ReviewConverter.toMyReviewListResponse(page, hasNext, nextLastId, nextLastStar);
    }
}
