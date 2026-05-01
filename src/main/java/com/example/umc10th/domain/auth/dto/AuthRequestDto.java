package com.example.umc10th.domain.auth.dto;

import com.example.umc10th.domain.user.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.time.LocalDate;

public class AuthRequestDto {

    @Getter
    public static class SignupRequest {

        @NotBlank
        @Size(max = 50)
        private String nickname;

        @NotBlank
        @Email
        private String email;

        @NotBlank
        private String userName;

        @NotNull
        private Gender gender;

        @NotNull
        private LocalDate birthDate;

        @NotBlank
        private String addressMain;

        @NotBlank
        private String addressDetail;

        @NotBlank
        @Size(min = 5, max = 5)
        private String zipCode;
    }
}
