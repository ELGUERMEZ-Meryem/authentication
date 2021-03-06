package com.authentication.api.service;

import com.authentication.api.entity.User;
import com.authentication.api.enums.TwofaTypes;
import com.authentication.api.exception.EmailAlreadyExistException;
import com.authentication.api.exception.PhoneNumberAlreadyExistException;
import com.authentication.api.exception.PhoneNumberNotValidException;
import com.authentication.api.repository.UserRepository;
import com.twilio.exception.ApiException;
import com.twilio.rest.lookups.v1.PhoneNumber;
import org.jboss.aerogear.security.otp.Totp;
import org.jboss.aerogear.security.otp.api.Base32;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * User service
 */

@Service
public class UserService implements IUser {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TwilioService twilioService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, TwilioService twilioService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.twilioService = twilioService;
    }

    /**
     * Add user to database if he is not registered in it.
     * if 2fa is Enabled and 2fa type is Google authenticator we generate secret code and we set isEnabled to 0
     * else we set isEnabled to 1
     *
     * @param user data
     * @return User
     * @throws EmailAlreadyExistException
     * @throws PhoneNumberAlreadyExistException
     * @throws PhoneNumberNotValidException
     */
    @Override
    public User addUser(User user) throws EmailAlreadyExistException, PhoneNumberAlreadyExistException, PhoneNumberNotValidException {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new EmailAlreadyExistException("email already exist");
        }
        if (userRepository.findByPhoneNumber(user.getPhoneNumber()) != null) {
            throw new PhoneNumberAlreadyExistException("phone number already exist");
        }
        //check if phone number is a valid number
        if (!isPhoneNumberValid(user.getPhoneNumber())) {
            throw new PhoneNumberNotValidException("Phone number[" + user.getPhoneNumber() + "] is not a valid number");
        }


        if (user.getIs_2fa_enabled() != null && user.getIs_2fa_enabled()) {
            if (user.getDefault_type_2fa().equals(TwofaTypes.GoogleAuth)) {
                //if 2fa isEnabled and 2fa type is Google authenticator, generate 2fa secret key encoded in Base32 format
                //and isEnabled field is setting to 0 by default
                user.setCode_2fa(Base32.random());
            } else {
                //if 2fa isEnabled and 2fa type is sms, we set isEnabled to 1
                user.setIsEnabled(1);
            }
        } else {
            //if 2fa is not enabled we set isEnabled field to 1
            user.setIsEnabled(1);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user = userRepository.save(user);
        return user;
    }

    @Override
    public Boolean verifySecretCode(String username, String code) {
        User user = userRepository.findByEmail(username);
        //Get the user’s secret key from database and current time generate TOTP using mentioned algorithm.
        //the code has to be can parsed to long 
        //We compare this generated TOTP with the code
        Totp totp = new Totp(user.getCode_2fa());
        if (isValid(code) && totp.verify(code)) {
            user.setIsEnabled(1);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    /**
     * check if we can parse the code to long
     *
     * @param code to parse
     * @return true if the code has successfully parsed to long, false if he failed and throw NumberFormatException
     */
    private Boolean isValid(String code) {
        try {
            Long.parseLong(code);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

    /**
     * check if the number is a valid number
     *
     * @param toNumber number that we need to check
     * @return true if the number is valid
     */
    private boolean isPhoneNumberValid(String toNumber) {
        try {
            twilioService.initTwilio();
            PhoneNumber.fetcher(new com.twilio.type.PhoneNumber(toNumber)).fetch();
            return true;
        } catch (ApiException e) {
            return false;
        }
    }
}
