package com.spot.auth.security;

import com.spot.auth.model.CustomUserDetails;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
@Log4j2
public class JwtTokenProvider {

    @Value("${jwt.secret:secretKeyAnything1234567890abcdefghijklmnopqrstuvwxyz}")
    private String secretKey;

    @Autowired
    private ApplicationContext applicationContext;

    public Authentication getAuthentication(String token) {
        UserDetailsService userDetailsService = applicationContext.getBean(UserDetailsService.class);
        String username = getUsernameFromToken(token);
        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String createToken(String username, long validityInMilliseconds) {
        Claims claims = Jwts.claims().setSubject(username);
        Date now = new Date();

        JwtBuilder builder = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .signWith(SignatureAlgorithm.HS256, secretKey.getBytes(StandardCharsets.UTF_8));

        if (validityInMilliseconds > 0) {
            Date validity = new Date(now.getTime() + validityInMilliseconds);
            builder.setExpiration(validity);
        }

        return builder.compact();
    }

    public boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

   /* public Authentication getAuthentication(String token) {
        UserDetails userDetails = new User(getUsernameFromToken(token), "", Collections.singletonList(new SimpleGrantedAuthority("USER")));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }*/

    private Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parser().setSigningKey(secretKey.getBytes(StandardCharsets.UTF_8)).parseClaimsJws(token).getBody();
        } catch (Exception e) {
            log.error("Error parsing claims from token: {}", e.getMessage());
            throw e;
        }
    }


    public String getUsernameFromToken(String token) {
        return getAllClaimsFromToken(token).getSubject();
    }

    private boolean isTokenExpired(String token) {
        final Date expiration = getAllClaimsFromToken(token).getExpiration();
        if (expiration != null) {
            boolean isExpired = expiration.before(new Date());
            log.debug("Checking if token is expired: {}", isExpired);
            return isExpired;
        }
        log.debug("Token has no expiration date and is therefore not expired.");
        return false; // Tokens without an expiration are considered not expired
    }

}