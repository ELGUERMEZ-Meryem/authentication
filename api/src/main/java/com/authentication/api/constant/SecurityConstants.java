package com.authentication.api.constant;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * We can define some reusable constants and defaults for generation and validation of JWTs, this constants values are in application.yml file
 *
 */

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Data
public class SecurityConstants {
    //login URL /auth/login
    private String authLoginUrl;
    // Signing key for HS512 algorithm
    private String tokenSecret;
    // JWT token defaults
    private String tokenHeader;
    private String tokenPrefix;
    private Long expiration;
    private String sub;
    private String create;
}
