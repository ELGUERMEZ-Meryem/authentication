package com.authentication.api.security;

import com.authentication.api.constant.SecurityConstants;
import com.authentication.api.entity.User;
import com.authentication.api.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final SecurityConstants securityConstants;
    private final UserRepository userRepository;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, SecurityConstants securityConstants, UserRepository userRepository) {
        this.authenticationManager = authenticationManager;
        this.securityConstants = securityConstants;
        this.userRepository = userRepository;
        setFilterProcessesUrl(securityConstants.getAuthLoginUrl());
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        final Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(""+request.getAttribute("email"), ""+request.getAttribute("password")));
        User user = userRepository.findByEmail(""+request.getAttribute("email"));
        if (user.isEmpty()) {
            throw new BadCredentialsException("Invalid username or password");
        }
        return authentication;
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain filterChain, Authentication authentication) throws IOException {
        User user = ((User) authentication.getPrincipal());
        String token = generateToken(user.getEmail());
        response.getWriter().write(new ObjectMapper().writeValueAsString(token));
    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        if (failed instanceof InsufficientAuthenticationException) {
            response.setStatus(HttpServletResponse.SC_ACCEPTED);
        } else {
            super.unsuccessfulAuthentication(request, response, failed);
        }
    }

    public String generateToken(String email) {

        Map<String, Object> claims = new HashMap<>();
        claims.put(securityConstants.getSub(), email);
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
}
