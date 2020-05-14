package com.authentication.api.security;

import com.authentication.api.constant.SecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class TokenUtil {
    private final SecurityConstants securityConstants;

    public TokenUtil(SecurityConstants securityConstants) {
        this.securityConstants = securityConstants;
    }

    public String generateToken(UserDetails userDetails) {

        Map<String, Object> claims = new HashMap<>();
        claims.put(securityConstants.getSub(), userDetails.getUsername());
        claims.put(securityConstants.getCreate(), new Date());
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(generateExpirationDate())
                .signWith(SignatureAlgorithm.HS512, securityConstants.getTokenSecret())
                .compact();
    }

    public String getUserNameFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(securityConstants.getTokenSecret())
                    .parseClaimsJws(token)
                    .getBody();
            return (String) claims.get(securityConstants.getSub());

        } catch(Exception e){
            return null;
        }
    }

    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + securityConstants.getExpiration() * 1000);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = getUserNameFromToken(token);
        return ((username.equals(userDetails.getUsername())) && !isTokenExpired(token));

    }

    private boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(securityConstants.getTokenSecret())
                    .parseClaimsJws(token)
                    .getBody();
            Date expirationDate = claims.getExpiration();
            return expirationDate.before(new Date());
        }
        catch (Exception e) {
            return false;
        }
    }

}
