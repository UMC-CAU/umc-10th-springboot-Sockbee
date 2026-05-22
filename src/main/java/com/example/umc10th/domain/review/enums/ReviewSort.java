package com.example.umc10th.domain.review.enums;

/**
 * 내 리뷰 목록 조회 정렬 기준.
 * - ID: 최신 등록순 (id DESC)
 * - STAR: 별점 높은 순 (star DESC, id DESC tie-break)
 */
public enum ReviewSort {
    ID,
    STAR
}
