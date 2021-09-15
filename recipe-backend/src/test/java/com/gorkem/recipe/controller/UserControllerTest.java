package com.gorkem.recipe.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gorkem.recipe.exception.UserAlreadyExistsException;
import com.gorkem.recipe.payload.request.SignInRequest;
import com.gorkem.recipe.payload.request.SignUpRequest;

/**
 * User controller integration test class.
 * 
 * @author gorkemdemiray
 */
@SpringBootTest
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class UserControllerTest {

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	private ObjectMapper objectMapper;

	private MockMvc mockMvc;

	@BeforeEach
	public void setUp() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	}

	@Test
	void GivenInvalidCredentials_WhenTriedForRegistration_ThenResponseIsBadRequest() throws Exception {
		SignUpRequest signUpRequest = SignUpRequest.builder()
				.username("")
				.email("")
				.password("")
				.build();

		String content = objectMapper.writeValueAsString(signUpRequest);

		final Exception exception = mockMvc.perform(post("/api/auth/signup")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn()
				.getResolvedException();
		
		assertThat(exception).isExactlyInstanceOf(MethodArgumentNotValidException.class);
	}

	@Test
	void GivenValidCredentials_WhenTriedForRegistration_ThenResponseIsCreated() throws Exception {
		SignUpRequest signUpRequest = SignUpRequest.builder()
				.username("username")
				.email("user@user.com")
				.password("password")
				.build();

		String content = objectMapper.writeValueAsString(signUpRequest);

		mockMvc.perform(post("/api/auth/signup")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.message").isNotEmpty());
	}
	
	@Test
	void GivenValidCredentials_WhenTriedForRegistrationWithExistingUsername_ThenResponseIsConflict() throws Exception {
		GivenValidCredentials_WhenTriedForRegistration_ThenResponseIsCreated();
		
		SignUpRequest signUpRequest = SignUpRequest.builder()
				.username("username")
				.email("user@user.com")
				.password("password")
				.build();

		String content = objectMapper.writeValueAsString(signUpRequest);

		final Exception exception = mockMvc.perform(post("/api/auth/signup")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isConflict())
				.andReturn()
				.getResolvedException();
		
		assertThat(exception).isExactlyInstanceOf(UserAlreadyExistsException.class);
	}

	@Test
	void GivenNonExistingCredentials_WhenTriedForLogin_ThenResponseIsBadRequest() throws Exception {
		SignInRequest signInRequest = SignInRequest.builder()
				.username("username")
				.password("password")
				.build();

		String content = objectMapper.writeValueAsString(signInRequest);

		final Exception exception = mockMvc.perform(post("/api/auth/signin")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn()
				.getResolvedException();
		
		assertThat(exception).isExactlyInstanceOf(InternalAuthenticationServiceException.class);
	}
	
	@Test
	void GivenWrongCredentials_WhenTriedForLogin_ThenResponseIsBadRequest() throws Exception {
		GivenValidCredentials_WhenTriedForRegistration_ThenResponseIsCreated();
		
		SignInRequest signInRequest = SignInRequest.builder()
				.username("username")
				.password("password1234")
				.build();

		String content = objectMapper.writeValueAsString(signInRequest);

		final Exception exception = mockMvc.perform(post("/api/auth/signin")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn()
				.getResolvedException();
		
		assertThat(exception).isExactlyInstanceOf(BadCredentialsException.class);
	}
	
	@Test
	void GivenValidCredentials_WhenTriedForLogin_ThenResponseIsOk() throws Exception {
		GivenValidCredentials_WhenTriedForRegistration_ThenResponseIsCreated();

		SignInRequest signInRequest = SignInRequest.builder()
				.username("username")
				.password("password")
				.build();

		String content = objectMapper.writeValueAsString(signInRequest);

		mockMvc.perform(post("/api/auth/signin")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.message").isNotEmpty());
	}
}
