package com.authentication.api.service;

import com.authentication.api.entity.User;
import com.authentication.api.exception.EmailAlreadyExistException;
import com.authentication.api.repository.UserRepository;
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
        System.out.println("generate code here    "+user);

        if (user.getIs_2fa_enabled() != null && user.getIs_2fa_enabled()) {
            System.out.println("generate code here    "+user);
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user = userRepository.save(user);
        return user;
    }
}
