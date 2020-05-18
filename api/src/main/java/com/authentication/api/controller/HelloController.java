package com.authentication.api.controller;

import com.authentication.api.constant.SecurityConstants;
import com.authentication.api.entity.User;
import com.authentication.api.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/public")
public class HelloController {
    private final SecurityConstants securityConstants;

    @Autowired
    private AuthenticationManager authenticationManager;

    private final UserRepository userRepository;

    public HelloController(SecurityConstants securityConstants, UserRepository userRepository) {
        this.securityConstants = securityConstants;
        this.userRepository = userRepository;
    }

    @PostMapping("/hey")
    public String singIn(@RequestBody User user) {
        final Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = generateToken(user);
        return token;
    }

    public String generateToken(User user) {

        Map<String, Object> claims = new HashMap<>();
        claims.put(securityConstants.getSub(), user.getEmail());
        claims.put(securityConstants.getCreate(), new Date());
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(generateExpirationDate())
                .signWith(SignatureAlgorithm.HS512, securityConstants.getTokenSecret())
                .compact();
    }

    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + securityConstants.getExpiration() * 1000);
    }

    @PostMapping("/signUp")
    public User singUp(@RequestBody User user) {
        user = userRepository.save(user);
        return user;
    }
}
