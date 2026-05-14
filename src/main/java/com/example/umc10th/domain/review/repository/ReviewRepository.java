package com.example.umc10th.domain.review.repository;

import com.example.umc10th.domain.review.entity.Review;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * 내 리뷰 목록 — ID 내림차순 (단일 커서: lastId).
     */
    @Query("""
            SELECT r FROM Review r
            JOIN FETCH r.store s
            WHERE r.user.id = :userId
              AND (:lastId IS NULL OR r.id < :lastId)
            ORDER BY r.id DESC
            """)
    List<Review> findMyReviewsOrderById(
            @Param("userId") Long userId,
            @Param("lastId") Long lastId,
            Pageable pageable
    );

    /**
     * 내 리뷰 목록 — 별점 내림차순 (복합 커서: lastStar + lastId).
     * 같은 별점 내에서 id 내림차순으로 tie-break.
     */
    @Query("""
            SELECT r FROM Review r
            JOIN FETCH r.store s
            WHERE r.user.id = :userId
              AND (:lastId IS NULL
                   OR r.star < :lastStar
                   OR (r.star = :lastStar AND r.id < :lastId))
            ORDER BY r.star DESC, r.id DESC
            """)
    List<Review> findMyReviewsOrderByStar(
            @Param("userId") Long userId,
            @Param("lastStar") Float lastStar,
            @Param("lastId") Long lastId,
            Pageable pageable
    );
}
