package com.authentication.api.security;

import com.authentication.api.entity.User;
import com.authentication.api.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //To help Spring Security loading user-specific data in the framework.
    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByEmailAndIsEnabled(username, 1);
        if (user == null) {
            throw new UsernameNotFoundException(username);
        }
        return new UserDetail(user);
    }
}