package com.gorkem.recipe.payload.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.Builder;
import lombok.Data;

/**
 * Sign up request.
 * 
 * @author gorkemdemiray
 */
@Data
@Builder
public class SignUpRequest {
	
	@NotBlank(message = "User name can not be empty!")
	@Size(max = 30, message = "User name can not be more than 30 characters!")
	private String username;
	
	@NotBlank(message = "Email can not be empty!")
	@Size(max = 50, message = "Email can not be more than 50 characters!")
	@Email(message = "It should be a valid email address!")
	private String email;
	
	@NotBlank(message = "Password can not be empty!")
	@Size(max = 100, message = "Password can not be more than 100 characters!")
	private String password;
}
