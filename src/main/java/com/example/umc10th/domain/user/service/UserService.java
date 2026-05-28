package com.example.umc10th.domain.user.service;

import com.example.umc10th.domain.user.converter.UserConverter;
import com.example.umc10th.domain.user.dto.UserResponseDto;
import com.example.umc10th.global.security.AuthMember;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    /**
     * 마이페이지 — 인증된 사용자의 내 정보 조회.
     * SecurityContext의 AuthMember에서 User 엔티티를 꺼내 응답 DTO로 변환한다.
     */
    public UserResponseDto.GetInfo getInfo(AuthMember member) {
        return UserConverter.toGetInfo(member.getMember());
    }
}
