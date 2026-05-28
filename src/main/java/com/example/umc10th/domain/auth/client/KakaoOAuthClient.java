package com.example.umc10th.domain.auth.client;

import com.example.umc10th.domain.auth.dto.KakaoTokenResponse;
import com.example.umc10th.domain.auth.dto.KakaoUserResponse;
import com.example.umc10th.domain.auth.exception.AuthErrorCode;
import com.example.umc10th.domain.auth.exception.AuthException;
import com.example.umc10th.global.config.KakaoOAuthProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

/**
 * 카카오 OAuth 인증 서버와 통신하는 클라이언트.
 *
 * - 토큰 발급:   POST https://kauth.kakao.com/oauth/token   (form url-encoded)
 * - 유저 정보:   GET  https://kapi.kakao.com/v2/user/me     (Bearer)
 */
@Component
@RequiredArgsConstructor
public class KakaoOAuthClient {

    private static final String KAUTH_BASE = "https://kauth.kakao.com";
    private static final String KAPI_BASE = "https://kapi.kakao.com";

    private final KakaoOAuthProperties properties;

    /**
     * Authorization Code → AccessToken 교환.
     */
    public KakaoTokenResponse exchangeCodeForToken(String code) {
        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("client_id", properties.getClientId());
        form.add("client_secret", properties.getClientSecret());
        form.add("redirect_uri", properties.getRedirectUri());
        form.add("code", code);

        try {
            return RestClient.create(KAUTH_BASE)
                    .post()
                    .uri("/oauth/token")
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .body(form)
                    .retrieve()
                    .onStatus(status -> status.isError(), (req, res) -> {
                        throw new AuthException(AuthErrorCode.KAKAO_TOKEN_FETCH_FAILED);
                    })
                    .body(KakaoTokenResponse.class);
        } catch (AuthException e) {
            throw e;
        } catch (Exception e) {
            throw new AuthException(AuthErrorCode.KAKAO_TOKEN_FETCH_FAILED);
        }
    }

    /**
     * AccessToken으로 카카오 유저 정보 조회.
     */
    public KakaoUserResponse fetchUserInfo(String accessToken) {
        try {
            return RestClient.create(KAPI_BASE)
                    .get()
                    .uri("/v2/user/me")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                    .retrieve()
                    .onStatus(status -> status.isError(), (req, res) -> {
                        throw new AuthException(AuthErrorCode.KAKAO_USER_INFO_FETCH_FAILED);
                    })
                    .body(KakaoUserResponse.class);
        } catch (AuthException e) {
            throw e;
        } catch (Exception e) {
            throw new AuthException(AuthErrorCode.KAKAO_USER_INFO_FETCH_FAILED);
        }
    }
}
