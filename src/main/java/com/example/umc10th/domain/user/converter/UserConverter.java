package com.example.umc10th.domain.user.converter;

import com.example.umc10th.domain.auth.dto.AuthRequestDto;
import com.example.umc10th.domain.auth.dto.AuthResponseDto;
import com.example.umc10th.domain.user.entity.User;
import com.example.umc10th.domain.user.enums.UserStatus;

public class UserConverter {

    public static User toUser(AuthRequestDto.SignupRequest req, String encodedPassword) {
        return User.builder()
                .nickname(req.nickname())
                .email(req.email())
                .password(encodedPassword)
                .name(req.userName())
                .gender(req.gender())
                .birthDate(req.birthDate())
                .addressMain(req.addressMain())
                .addressDetail(req.addressDetail())
                .zipCode(req.zipCode())
                .status(UserStatus.ACTIVE)
                .missionCount(0)
                .point(0)
                .build();
    }

    public static AuthResponseDto.SignupResponse toSignupResponse(User user) {
        return AuthResponseDto.SignupResponse.builder()
                .userId(user.getId())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
