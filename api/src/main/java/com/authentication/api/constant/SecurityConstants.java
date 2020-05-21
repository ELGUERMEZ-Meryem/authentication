package com.authentication.api.constant;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Data
public class SecurityConstants {
    //login URL /auth/login
    private String authLoginUrl;
    private String tokenSecret;
    private String tokenHeader;
    private String tokenPrefix;
    private Long expiration;
    private String sub;
    private String create;
}
