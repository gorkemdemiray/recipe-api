package com.gorkem.recipe.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

/**
 * Authentication entry point to be used in security configurer.
 * 
 * @author gorkemdemiray
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint{
	
	private static final String UNAUTHORIZED = "Unauthorized";

	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, UNAUTHORIZED);
	}

}
