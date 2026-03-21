package com.example.tiffin_center_management.util;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;



@Component
@Slf4j
public class JwtUtil {

	 @Value("${jwt.secret}")
	    private String secret;

	    @Value("${jwt.expiration}")
	    private long expiration;

	    private Key key;

	    // Secret ko secure key me convert karega
	    @PostConstruct
	    public void init() {
	        this.key = Keys.hmacShaKeyFor(secret.getBytes());
	    }

	    // Token Generate
	    public String generateToken(String email) {
	        return Jwts.builder()
	                .setSubject(email) // user identity
	                .setIssuedAt(new Date())
	                .setExpiration(new Date(System.currentTimeMillis() + expiration))
	                .signWith(key, SignatureAlgorithm.HS256)
	                .compact();
	    }

	    // Email Extract
	    public String extractEmail(String token) {
	        return getClaims(token).getSubject();
	    }

	    // Token Validate
	    public boolean validateToken(String token, String email) {
	        try {
	            final String extractedEmail = extractEmail(token);
	            return extractedEmail.equals(email) && !isTokenExpired(token);
	        } catch (Exception e) {
	            log.error("JWT validation failed: {}", e.getMessage());
	            return false;
	        }
	    }

	    // Check Expiry
	    private boolean isTokenExpired(String token) {
	        return getClaims(token).getExpiration().before(new Date());
	    }

	    //Claims Extract
	    private Claims getClaims(String token) {
	        return Jwts.parserBuilder()
	                .setSigningKey(key)
	                .build()
	                .parseClaimsJws(token)
	                .getBody();
	    }
	
}
