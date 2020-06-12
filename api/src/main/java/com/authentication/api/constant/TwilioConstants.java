package com.authentication.api.constant;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * We define some reusable constants and defaults to send an sms, this constants values are in application.yml file
 */
@Configuration
@ConfigurationProperties(prefix = "twilio")
@Data
public class TwilioConstants {
    //phone number that we are going to send sms with
    private String trialNumber;
    //account sid and token to authenticate to our twilio account
    private String accountSid;
    private String authToken;
}
