package com.authentication.api.service;

/**
 * Twilio interface
 */
public interface ITwilio {
    /**
     * Initialize the Twilio account
     */
    void initTwilio();

    /**
     * send sms to number that we get in parameter with a message that contain the verification number that we get in parameter
     *
     * @param toNumber the number to send message to
     * @param message  a number to verify that we are going to send it in the message
     * @return true if the sms sanded with successs
     */
    boolean SendSMS(String toNumber, int message);
}
