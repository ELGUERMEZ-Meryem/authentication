package com.authentication.api.service;

import com.authentication.api.entity.User;
import com.authentication.api.exception.EmailAlreadyExistException;
import com.authentication.api.repository.UserRepository;
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
     * Add user to database if he is not registered in it
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
            user.setCode_2fa(Base32.random());
        } else {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            user = userRepository.save(user);
        }
        return user;
    }

    @Override
    public User verifySecretCode(String username, String code) {
        System.out.println("hey from verification code"+code+" username "+username);
        User user = userRepository.findByEmail(username);
        user.setCode_2fa(code);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
}
