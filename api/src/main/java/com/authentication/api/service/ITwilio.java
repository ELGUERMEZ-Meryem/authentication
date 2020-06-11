package com.authentication.api.service;

public interface ITwilio {
    void initTwilio(String accountSID, String authToken);
    boolean SendSMS(String toNumber, String message);
}
