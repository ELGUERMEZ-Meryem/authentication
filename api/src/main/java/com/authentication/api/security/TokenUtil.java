package com.authentication.api.security;

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

    @Value("${auth.sub}")
    private final String CLAIMS_SUBJECT = "sub";

    @Value("${auth.create}")
    private final String CLAIMS_CREATE = "created";

    @Value("${auth.expiration}")
    private Long TOKEN_VALIDITY = 604800L;

    @Value("${auth.secret}")
    private String TOKEN_Secret;

    public String generateToken(UserDetails userDetails) {

        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIMS_SUBJECT, userDetails.getUsername());
        claims.put(CLAIMS_CREATE, new Date());
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(generateExpirationDate())
                .signWith(SignatureAlgorithm.HS512, TOKEN_Secret)
                .compact();
    }

    public String getUserNameFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(TOKEN_Secret)
                    .parseClaimsJws(token)
                    .getBody();
            return (String) claims.get(CLAIMS_SUBJECT);

        } catch(Exception e){
            return null;
        }
    }

    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + TOKEN_VALIDITY * 1000);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        String username = getUserNameFromToken(token);
        return ((username.equals(userDetails.getUsername())) && !isTokenExpired(token));

    }

    private boolean isTokenExpired(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(TOKEN_Secret)
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
