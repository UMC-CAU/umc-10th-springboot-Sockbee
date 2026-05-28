package com.example.umc10th.domain.auth.repository;

import com.example.umc10th.domain.auth.entity.OAuth;
import com.example.umc10th.domain.auth.enums.OAuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OAuthRepository extends JpaRepository<OAuth, Long> {
    Optional<OAuth> findByProviderAndProviderUserId(OAuthProvider provider, Long providerUserId);
}
