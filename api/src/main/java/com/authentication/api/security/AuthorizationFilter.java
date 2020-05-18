package com.authentication.api.security;

import com.authentication.api.constant.SecurityConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthorizationFilter extends OncePerRequestFilter {
    private final SecurityConstants securityConstants;

    @Autowired
    private TokenUtil tokenUtil;

    @Autowired
    private UserService userService;

    public AuthorizationFilter(SecurityConstants securityConstants) {
        this.securityConstants = securityConstants;
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
                UserDetails user = userService.loadUserByUsername(username);
                if (tokenUtil.isTokenValid(token, user)) {
                    return new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                }
            }
        }
        return null;
    }
}
