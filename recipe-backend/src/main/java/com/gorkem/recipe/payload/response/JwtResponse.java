package com.gorkem.recipe.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Jwt response.
 * 
 * @author gorkemdemiray
 */
@Data
@AllArgsConstructor
public class JwtResponse {

	private String jwt;
	private String message;
}
