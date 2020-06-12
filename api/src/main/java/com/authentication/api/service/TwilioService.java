package com.authentication.api.service;

import com.authentication.api.constant.TwilioConstants;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.api.v2010.account.MessageCreator;
import com.twilio.type.PhoneNumber;
import org.springframework.stereotype.Service;

@Service
public class TwilioService implements ITwilio {
    private final TwilioConstants twilioConstants;

    public TwilioService(TwilioConstants twilioConstants) {
        this.twilioConstants = twilioConstants;
    }

    @Override
    public void initTwilio() {
        Twilio.init(twilioConstants.getAccountSid(), twilioConstants.getAuthToken());
    }

    @Override
    public boolean SendSMS(String toNumber, int ra) {
        if(isPhoneNumber(toNumber)){
            PhoneNumber to = new PhoneNumber(toNumber);
            PhoneNumber from = new PhoneNumber(twilioConstants.getTrialNumber());
            String message = "Hey, your verification code is: "+ ra + ", this code will expired after 5 minutes";
            MessageCreator creator = Message.creator(to, from, message);
            creator.create();
            return true;
        } else {
            throw new IllegalArgumentException("Phone number["+ toNumber + "] is not a valid number");
        }
    }

    private boolean isPhoneNumber(String toNumber) {
        //String regexPattern = "\\+\\d(-\\d{3}){2}-\\d{4}";
        //return toNumber.matches(regexPattern);
        return true;
    }
}
