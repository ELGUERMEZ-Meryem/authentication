package com.authentication.api.security;

import com.authentication.api.constant.SecurityConstants;
import com.authentication.api.entity.User;
import com.authentication.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.filter.OncePerRequestFilter;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthorizationFilter extends OncePerRequestFilter {
    private final SecurityConstants securityConstants;
    private final UserRepository userRepository;

    @Autowired
    private TokenUtil tokenUtil;

    public AuthorizationFilter(SecurityConstants securityConstants, UserRepository userRepository) {
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
            String username = tokenUtil.getUserNameFromToken(token);
            if (StringUtils.isNotEmpty(username)) {
                User user = userRepository.findByEmail(username);
                if (tokenUtil.isTokenValid(token, user)) {
                    return new UsernamePasswordAuthenticationToken(user, null, null);
                }
            }
        }
        return null;
    }
}
