package com.authentication.api.constant;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Data
public class SecurityConstants {
    private String authLoginUrl;
    // Signing key for HS512 algorithm
    // HS512 algorithm needs a key with size at least 512 bytes.
    private String tokenSecret;
    // JWT token defaults
    private String tokenHeader;
    private Long expiration;
    private String sub;
    private String create;
}
