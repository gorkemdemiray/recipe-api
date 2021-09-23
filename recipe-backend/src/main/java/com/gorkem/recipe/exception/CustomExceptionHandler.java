package com.gorkem.recipe.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

/**
 * Handles specified exceptions and returns human readable responses
 * 
 * @author gorkemdemiray
 */
@ControllerAdvice
public class CustomExceptionHandler {

	@ExceptionHandler({ NoRecipesFoundException.class, RecipeNotFoundException.class, UserNotFoundException.class })
	public ResponseEntity<ErrorResponse> handleNotFoundException(final Exception exception) {
		ErrorResponse response = new ErrorResponse(exception.getMessage(), HttpStatus.NOT_FOUND);
		return new ResponseEntity<>(response, response.getStatus());
	}

	@ExceptionHandler({ RecipeAlreadyExistsException.class, UserAlreadyExistsException.class })
	public ResponseEntity<ErrorResponse> handleAlreadyExistsException(final Exception exception) {
		ErrorResponse response = new ErrorResponse(exception.getMessage(), HttpStatus.CONFLICT);
		return new ResponseEntity<>(response, response.getStatus());
	}
	
	@ExceptionHandler({ BadCredentialsException.class, InternalAuthenticationServiceException.class })
	public ResponseEntity<ErrorResponse> handleUnauthorizedException(final Exception exception) {
		ErrorResponse response = new ErrorResponse(exception.getMessage(), HttpStatus.UNAUTHORIZED);
		return new ResponseEntity<>(response, response.getStatus());
	}
	
	@ExceptionHandler({ ExpiredJwtException.class, MalformedJwtException.class, SignatureException.class, UnsupportedJwtException.class })
	public ResponseEntity<ErrorResponse> handleJwtException(final Exception exception) {
		ErrorResponse response = new ErrorResponse(exception.getMessage(), HttpStatus.FORBIDDEN);
		return new ResponseEntity<>(response, response.getStatus());
	}
}
