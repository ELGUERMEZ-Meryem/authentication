package com.authentication.api.controller;

import com.authentication.api.entity.User;
import com.authentication.api.repository.UserRepository;
import com.authentication.api.security.TokenUtil;
import com.authentication.api.security.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public")
public class HelloController {

    @Autowired
    private TokenUtil tokenUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/g")
    public String s(){

        return "kkkkkkkkkkkkkkkkkkkkkkk";
    }

    @PostMapping("/hey")
    public String singIn(@RequestBody User user){
        final Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetails userDetails = userService.loadUserByUsername(user.getEmail());
        String token = tokenUtil.generateToken(userDetails);
        return token;
    }
    @PostMapping("/signUp")
    public User singUp(@RequestBody User user){
        user = userRepository.save(user);
        return user;
    }
}
