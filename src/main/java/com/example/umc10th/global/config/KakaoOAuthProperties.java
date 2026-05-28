package com.example.umc10th.global.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "kakao")
public class KakaoOAuthProperties {
    private String clientId;
    private String clientSecret;
    private String redirectUri;
}
