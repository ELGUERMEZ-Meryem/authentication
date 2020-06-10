package com.authentication.api.service;

import com.authentication.api.entity.User;
import com.authentication.api.exception.EmailAlreadyExistException;
import com.authentication.api.repository.UserRepository;
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

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Add user to database if he is not registered in it.
     * if 2fa is Enabled we generate secret code and we set isEnabled to 0
     * else we set isEnabled to 1
     *
     * @param user data
     * @return User
     * @throws EmailAlreadyExistException
     */
    @Override
    public User addUser(User user) throws EmailAlreadyExistException {
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new EmailAlreadyExistException("email already exist");
        }

        if (user.getIs_2fa_enabled() != null && user.getIs_2fa_enabled()) {
            //if 2fa isEnabled, generate 2fa secret key encoded in Base32 format
            //and isEnabled field is setting to 0 by default
            user.setCode_2fa(Base32.random());
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
}
