package com.authentication.api.constant;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "twilio")
@Data
public class TwilioConstants {
    private String trialNumber;
    private String accountSid;
    private String authToken;
}
