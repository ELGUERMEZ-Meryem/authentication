package com.authentication.api.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * this model help us to send a response body when the authentication is not completed
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Response {
    boolean isNotEnabled;
    boolean isEnabled2fa;
    boolean isSMSCodeSanded;
    String code_2fa;
}
