package com.authentication.api.security;

import com.authentication.api.constant.SecurityConstants;
import com.authentication.api.entity.User;
import com.authentication.api.repository.UserRepository;
import io.jsonwebtoken.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
    private final SecurityConstants securityConstants;
    private final UserRepository userRepository;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, SecurityConstants securityConstants, UserRepository userRepository) {
        super(authenticationManager);
        this.securityConstants = securityConstants;
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
        if (authentication == null) {
            filterChain.doFilter(request, response);
            return;
        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        final String token = request.getHeader(securityConstants.getTokenHeader());
        if (StringUtils.isNotEmpty(token) && token.startsWith(securityConstants.getTokenPrefix())) {
            try {
                String username = getUserNameFromToken(token);
                if (StringUtils.isNotEmpty(username)) {
                    User user = userRepository.findByEmail(username);
                    return new UsernamePasswordAuthenticationToken(user, null, null);
                }
            } catch (ExpiredJwtException exception) {
                System.out.println("Expired JWT : " + exception.getMessage());
            } catch (UnsupportedJwtException exception) {
                System.out.println("Unsupported JWT : " + exception.getMessage());
            } catch (MalformedJwtException exception) {
                System.out.println("Invalid JWT : " + exception.getMessage());
            } catch (SignatureException exception) {
                System.out.println("Invalid signature : " + exception.getMessage());
            } catch (IllegalArgumentException exception) {
                System.out.println("Empty or null JWT : " + exception.getMessage());
            }
        }
        return null;
    }

    public String getUserNameFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(securityConstants.getTokenSecret())
                    .parseClaimsJws(token.replace(securityConstants.getTokenPrefix(), ""))
                    .getBody();
            return (String) claims.get(securityConstants.getSub());

        } catch (Exception e) {
            return null;
        }
    }

    public boolean isTokenValid(String token, User user) {
        String username = getUserNameFromToken(token);
        return ((username.equals(user.getEmail())) && !isTokenExpired(token));

    }

    private boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(securityConstants.getTokenSecret())
                    .parseClaimsJws(token)
                    .getBody();
            Date expirationDate = claims.getExpiration();
            return expirationDate.before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
