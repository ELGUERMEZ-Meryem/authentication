package com.authentication.api.controller;

import com.authentication.api.entity.User;
import com.authentication.api.repository.UserRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public")
public class HelloController {

    private final UserRepository userRepository;

    public HelloController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostMapping("/signUp")
    public User singUp(@RequestBody User user) {
        user = userRepository.save(user);
        return user;
    }
}
