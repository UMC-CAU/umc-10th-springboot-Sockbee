package com.example.umc10th.domain.auth.dto;

import com.example.umc10th.domain.user.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class AuthRequestDto {

    public record SignupRequest(
            @NotBlank @Size(max = 50) String nickname,
            @NotBlank @Email String email,
            @NotBlank String password,
            @NotBlank String userName,
            @NotNull Gender gender,
            @NotNull LocalDate birthDate,
            @NotBlank String addressMain,
            @NotBlank String addressDetail,
            @NotBlank @Size(min = 5, max = 5) String zipCode
    ) {}

    public record LoginRequest(
            @NotBlank @Email String email,
            @NotBlank String password
    ) {}

    public record KakaoLoginRequest(
            @NotBlank String code
    ) {}

    public record KakaoSignupRequest(
            @NotBlank String signupToken,
            @NotBlank @Size(max = 50) String nickname,
            @NotBlank String userName,
            @NotNull Gender gender,
            @NotNull LocalDate birthDate,
            @NotBlank String addressMain,
            @NotBlank String addressDetail,
            @NotBlank @Size(min = 5, max = 5) String zipCode
    ) {}
}
