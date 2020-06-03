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

    //We parse the user's credentials and issue them to the AuthenticationManager.
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            User user = new ObjectMapper().readValue(request.getInputStream(), User.class);
            System.out.println("user from header:    "+user);
            User u = userRepository.findByEmail(user.getEmail());
            System.out.println("user from db:    "+u);
            if (u == null) {
                throw new BadCredentialsException("Invalid username or password");
            }
            if (u.getIs_2fa_enabled() != null && u.getIs_2fa_enabled()) {
                System.out.println("has enable 2fa and his 2fa code is:    "+u.getCode_2fa());
            }
            final Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));
            return authentication;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //This method called when a user successfully logs in. We use this method to generate a JWT for this user.
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain filterChain, Authentication authentication) throws IOException {
        UserDetail user = ((UserDetail) authentication.getPrincipal());
        String token = generateToken(user.getUsername());
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

    /**
     * Generates a JWT token containing username as subject, and token generate date as additional claims. These properties are taken from the specified
     * User object.
     *
     * @param email the user email for which the token will be generated
     * @return the JWT token
     */

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

    /**
     * Add number of days that the token is valid to today date
     *
     * @return expiration date
     */
    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + securityConstants.getExpiration() * 1000);
    }
}
