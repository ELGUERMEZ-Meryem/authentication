package com.authentication.api.service;

public interface ITwilio {
    void initTwilio();
    boolean SendSMS(String toNumber, int message);
}
