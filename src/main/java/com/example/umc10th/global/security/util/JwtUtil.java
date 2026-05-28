package com.example.umc10th.global.security.util;

import com.example.umc10th.domain.auth.dto.SignupTokenClaims;
import com.example.umc10th.domain.auth.enums.OAuthProvider;
import com.example.umc10th.global.security.AuthMember;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    private static final String CLAIM_TYPE = "type";
    private static final String TYPE_ACCESS = "access";
    private static final String TYPE_SIGNUP = "signup";

    private final SecretKey secretKey;
    private final Duration accessExpiration;
    private final Duration signupExpiration;

    public JwtUtil(
            @Value("${jwt.token.secretKey}") String secret,
            @Value("${jwt.token.expiration.access}") Long accessExpiration,
            @Value("${jwt.token.expiration.signup}") Long signupExpiration
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpiration = Duration.ofMillis(accessExpiration);
        this.signupExpiration = Duration.ofMillis(signupExpiration);
    }

    // ===== Access Token =====

    /** AccessToken 생성 (type=access claim 포함) */
    public String createAccessToken(AuthMember member) {
        Instant now = Instant.now();

        String authorities = member.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .subject(member.getUsername())
                .claim(CLAIM_TYPE, TYPE_ACCESS)
                .claim("role", authorities)
                .claim("email", member.getUsername())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(accessExpiration)))
                .signWith(secretKey)
                .compact();
    }

    /**
     * 토큰에서 이메일(subject) 가져오기 — access token 전용.
     * type=access 가 아니면 null을 반환해 인증 흐름에서 거부되도록 한다.
     */
    public String getEmail(String token) {
        try {
            Claims claims = getClaims(token).getPayload();
            if (!TYPE_ACCESS.equals(claims.get(CLAIM_TYPE, String.class))) {
                return null;
            }
            return claims.getSubject();
        } catch (JwtException e) {
            return null;
        }
    }

    /**
     * AccessToken 유효성 확인 — 서명/만료뿐 아니라 type=access 도 함께 검증.
     * signup 토큰이 Authorization 헤더로 잘못 사용되는 것을 차단한다.
     */
    public boolean isValid(String token) {
        try {
            Claims claims = getClaims(token).getPayload();
            return TYPE_ACCESS.equals(claims.get(CLAIM_TYPE, String.class));
        } catch (JwtException e) {
            return false;
        }
    }

    // ===== Signup Token (OAuth 임시 가입용) =====

    /**
     * OAuth 임시 가입 토큰 생성.
     * subject=email, type=signup, provider/providerUserId/nickname을 클레임으로 보관한다.
     */
    public String createSignupToken(OAuthProvider provider, Long providerUserId, String email, String nickname) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(email)
                .claim(CLAIM_TYPE, TYPE_SIGNUP)
                .claim("provider", provider.name())
                .claim("providerUserId", providerUserId)
                .claim("nickname", nickname)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(signupExpiration)))
                .signWith(secretKey)
                .compact();
    }

    /**
     * 임시 가입 토큰 파싱 — type=signup 인 경우에만 클레임을 반환.
     * 만료/서명 오류 또는 type 불일치 시 Optional.empty().
     */
    public Optional<SignupTokenClaims> parseSignupToken(String token) {
        try {
            Claims claims = getClaims(token).getPayload();
            if (!TYPE_SIGNUP.equals(claims.get(CLAIM_TYPE, String.class))) {
                return Optional.empty();
            }
            String providerName = claims.get("provider", String.class);
            Long providerUserId = claims.get("providerUserId", Long.class);
            String email = claims.getSubject();
            String nickname = claims.get("nickname", String.class);
            if (providerName == null || providerUserId == null || email == null) {
                return Optional.empty();
            }
            return Optional.of(new SignupTokenClaims(
                    OAuthProvider.valueOf(providerName),
                    providerUserId,
                    email,
                    nickname
            ));
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    // ===== 공통 =====

    private Jws<Claims> getClaims(String token) throws JwtException {
        return Jwts.parser()
                .verifyWith(secretKey)
                .clockSkewSeconds(60)
                .build()
                .parseSignedClaims(token);
    }
}
