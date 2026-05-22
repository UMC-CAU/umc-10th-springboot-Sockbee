package com.example.umc10th.global.apiPayload;

import lombok.Builder;
import org.springframework.data.domain.Slice;

import java.util.List;

/**
 * 커서 기반 페이지네이션 공통 응답 포맷.
 * - data       : 현재 페이지 아이템 리스트
 * - hasNext    : 다음 페이지 존재 여부 (Slice.hasNext()에서 자동 도출)
 * - nextCursor : 다음 요청에 그대로 넘기면 되는 opaque 문자열 (마지막 페이지면 null)
 * - pageSize   : 페이지 크기 (요청한 size)
 */
@Builder
public record Pagination<T>(
        List<T> data,
        Boolean hasNext,
        String nextCursor,
        Integer pageSize
) {
    public static <T> Pagination<T> of(Slice<T> slice, String nextCursor) {
        return Pagination.<T>builder()
                .data(slice.getContent())
                .hasNext(slice.hasNext())
                .nextCursor(slice.hasNext() ? nextCursor : null)
                .pageSize(slice.getSize())
                .build();
    }
}
