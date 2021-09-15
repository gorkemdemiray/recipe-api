package com.gorkem.recipe.security;

import java.util.Date;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.gorkem.recipe.service.UserDetailsImpl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class for generating and validating jwt tokens.
 * 
 * @author gorkemdemiray
 */
@Component
@Slf4j
public class JwtUtil {

	private static final String BEARER = "Bearer ";

	@Value("${recipe.app.jwtSecret}")
	private String jwtSecret;

	@Value("${recipe.app.jwtExpirationMs}")
	private int jwtExpirationMs;
	
	public String parseJwt(String authHeader) {
		if (authHeader != null && authHeader.startsWith(BEARER))
			return authHeader.substring(BEARER.length(), authHeader.length());
		return null;
	}
	
	public String generateJwtToken(Authentication authentication) {
		UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();
		
		return Jwts.builder()
				.setSubject(userPrincipal.getUsername())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
				.signWith(SignatureAlgorithm.HS512, jwtSecret)
				.compact();
	}

	private Claims getAllClaimsFromToken(String authToken) {
		try {
			return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken).getBody();
		} catch (Exception ex) {
			log.error(String.format("Invalid Jwt token: %s", ex.getMessage()));
			throw ex;
		}
	}
	
	public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}
	
	public String getUsernameFromToken(String token) {
		return getClaimFromToken(token, Claims::getSubject);
	}

	public Date getExpirationDateFromToken(String token) {
		return getClaimFromToken(token, Claims::getExpiration);
	}

	private boolean isTokenExpired(String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}
	
	public boolean validateJwtToken(String authToken, UserDetails userDetails) {
		final String username = getUsernameFromToken(authToken);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(authToken));
	}
}
