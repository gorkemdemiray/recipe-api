package com.gorkem.recipe.payload.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Builder;
import lombok.Data;

/**
 * Sign in request.
 * 
 * @author gorkemdemiray
 */
@Data
@Builder
public class SignInRequest {
	
	@NotBlank(message = "User name can not be empty!")
	@Size(max = 30, message = "User name can not be more than 30 characters!")
	private String username;
	
	@NotBlank(message = "Password can not be empty!")
	@Size(max = 30, message = "Password can not be more than 30 characters!")
	private String password;
}
