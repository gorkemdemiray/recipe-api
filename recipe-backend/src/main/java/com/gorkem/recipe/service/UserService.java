package com.gorkem.recipe.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.gorkem.recipe.exception.UserAlreadyExistsException;
import com.gorkem.recipe.model.User;
import com.gorkem.recipe.payload.request.SignInRequest;
import com.gorkem.recipe.payload.request.SignUpRequest;
import com.gorkem.recipe.repository.UserRepository;
import com.gorkem.recipe.security.JwtUtil;

/**
 * User service for all authentication operations
 * 
 * @author gorkemdemiray
 */
@Service
public class UserService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtUtil jwtUtil;

	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
			AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
	}

	public User registerUser(final SignUpRequest signUpRequest) {
		if (userRepository.existsByUsername(signUpRequest.getUsername()))
			throw new UserAlreadyExistsException(
					String.format("Username is already in use: %s", signUpRequest.getUsername()));

		if (userRepository.existsByEmail(signUpRequest.getEmail()))
			throw new UserAlreadyExistsException(
					String.format("Email is already in use: %s", signUpRequest.getEmail()));

		User user = User.builder().username(signUpRequest.getUsername()).email(signUpRequest.getEmail())
				.password(passwordEncoder.encode(signUpRequest.getPassword())).build();

		return userRepository.save(user);
	}

	public String authenticateUser(final SignInRequest signInRequest) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(signInRequest.getUsername(), signInRequest.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		return jwtUtil.generateJwtToken(authentication);
	}
}
