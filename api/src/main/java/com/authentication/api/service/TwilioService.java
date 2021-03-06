package com.authentication.api.service;

import com.authentication.api.constant.TwilioConstants;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import org.springframework.stereotype.Service;

/**
 * Twilio Service
 */
@Service
public class TwilioService implements ITwilio {
    private final TwilioConstants twilioConstants;

    public TwilioService(TwilioConstants twilioConstants) {
        this.twilioConstants = twilioConstants;
    }

    @Override
    public void initTwilio() {
        //initialize the Twilio account with ACCOUNT_SID and AUTH_ID
        Twilio.init(twilioConstants.getAccountSid(), twilioConstants.getAuthToken());
    }

    @Override
    public boolean SendSMS(String toNumber, int ra) {
        PhoneNumber to = new PhoneNumber(toNumber);
        PhoneNumber from = new PhoneNumber(twilioConstants.getTrialNumber());
        String message = "Hey, your verification code is: " + ra + ", this code will expired after 5 minutes";
        //create the message
        MessageCreator creator = Message.creator(to, from, message);
        //send the message
        creator.create();
        return true;
    }
}
