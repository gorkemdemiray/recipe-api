package com.gorkem.recipe.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gorkem.recipe.payload.request.SignInRequest;
import com.gorkem.recipe.payload.request.SignUpRequest;
import com.gorkem.recipe.payload.response.JwtResponse;
import com.gorkem.recipe.payload.response.MessageResponse;
import com.gorkem.recipe.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * User controller for all authentication operations
 * 
 * @author gorkemdemiray
 */
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class UserController {
	
	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@PostMapping("/signup")
	@Operation(summary = "Registers user with given credentials.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registers the user and returns the information message."),
            @ApiResponse(responseCode = "409", description = "Username or email is already in use.")
    })
	public ResponseEntity<MessageResponse> registerUser(@Valid @RequestBody SignUpRequest signUpRequest) {
		userService.registerUser(signUpRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(new MessageResponse("User registered successfully!"));
	}
	
	@PostMapping("/signin")
	@Operation(summary = "Authenticates user with given credentials.")
	@ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authenticates the user and returns jwt token."),
            @ApiResponse(responseCode = "400", description = "Invalid credentials.")
    })
	public ResponseEntity<JwtResponse> authenticateUser(@Valid @RequestBody SignInRequest signInRequest) {
		final String jwt = userService.authenticateUser(signInRequest);
		return ResponseEntity.status(HttpStatus.OK).body(new JwtResponse(jwt, "User is authenticated!"));
	}

}
