package com.app.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import org.springframework.stereotype.Component;


import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


@Component
@Slf4j
public class JwtTokenProvider {
	
	private final SecretKey secretKey; 
	private final long jwtExpiration;
	private final long refreshExpiration;
	
	public JwtTokenProvider(@Value("${jwt.secret}") String secret,
			@Value("${jwt.expiration}") long jwtExpiration,
			@Value("${jwt.refreshExpiration}") long refreshExpiration) {
		this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
		this.jwtExpiration = jwtExpiration;
		this.refreshExpiration = refreshExpiration;
	}
	
	public String generateToken(Authentication authentication) {
		
		return generateToken(authentication.getName(), authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority).collect(Collectors.toList()), jwtExpiration);
	}

	
	public String generateRefreshToken(Authentication authentication) {
		return generateToken(authentication.getName(), authentication.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority).collect(Collectors.toList()), refreshExpiration);
	}
	
	private String generateToken(String username, List<String> roles, long expiration) {
		Date now = new Date();
		Date expiryDate = new Date(now.getTime() + expiration);
		
		return Jwts.builder()
				.subject(username)
				.claim("roles", roles)
				.issuedAt(now)
				.expiration(expiryDate)
				.signWith(secretKey)
				.compact();
	}
	
	 public String getUsernameFromToken(String token) {
	        Claims claims = Jwts.parser()
	            .verifyWith(secretKey)
	            .build()
	            .parseSignedClaims(token)
	            .getPayload();
	        
	        return claims.getSubject();
	    }
	    
	    @SuppressWarnings("unchecked")
	    public List<String> getRolesFromToken(String token) {
	        Claims claims = Jwts.parser()
	            .verifyWith(secretKey)
	            .build()
	            .parseSignedClaims(token)
	            .getPayload();
	        
	        return (List<String>) claims.get("roles");
	    }
	    
	    public boolean validateToken(String token) {
	        try {
	            Jwts.parser()
	                .verifyWith(secretKey)
	                .build()
	                .parseSignedClaims(token);
	            return true;
	        } catch (JwtException | IllegalArgumentException e) {
	            log.error("Invalid JWT token: {}", e.getMessage());
	        }
	        return false;
	    }
	}


