package com.example.umc10th.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

public class UserResponseDto {

    /**
     * 마이페이지 — 내 정보 조회 응답.
     * (민감 필드: password, 주소는 제외)
     */
    @Getter
    @Builder
    public static class GetInfo {
        private Long userId;
        private String email;
        private String nickname;
        private String name;
        private int point;
        private int missionCount;
    }
}
