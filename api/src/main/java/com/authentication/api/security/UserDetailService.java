package com.authentication.api.security;

import com.authentication.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserDetailService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Bean
    private PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

//    @Override
//    public UserDetails loadUserByUsername(String username) {
//        com.authentication.api.entity.User user = userRepository.findByEmail(username);
//        if (user == null) {
//            System.out.println("User not found");
//        }
//        return new User(user.getEmail(), passwordEncoder().encode(user.getPassword()), AuthorityUtils.NO_AUTHORITIES);
//    }
}