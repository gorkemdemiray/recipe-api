package com.gorkem.recipe.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gorkem.recipe.exception.RecipeAlreadyExistsException;
import com.gorkem.recipe.exception.RecipeNotFoundException;
import com.gorkem.recipe.model.Recipe;
import com.gorkem.recipe.payload.request.SignInRequest;
import com.gorkem.recipe.payload.request.SignUpRequest;
import com.gorkem.recipe.payload.response.JwtResponse;
import com.gorkem.recipe.util.TestUtil;

import io.jsonwebtoken.SignatureException;

/**
 * Recipe controller integration test class.
 * 
 * @author gorkemdemiray
 */
@SpringBootTest
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class RecipeControllerTest {
	
	private static final String BEARER = "Bearer ";
	private static final String AUTHORIZATION = "Authorization";
	private static final String INVALID_JWT_TOKEN = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyIiwiaWF0IjoxNjMxNTc0NjcwLCJleHAiOjE2MzE2NjEwNzB9.pv_K_cmE35CJmfr4ajHgHvNfWGZjgm12GM2dWxnrc2ap_9vR0SI6M0eOAj-Td2ro75SDIoG10s622iNEUh3tUwst";

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
	void GivenWithoutJwtToken_WhenTriedForAPICall_ThenResponseIsBadRequest() throws Exception {
		String content = objectMapper.writeValueAsString(TestUtil.getRecipe());
		
		final Exception exception = mockMvc.perform(post("/api/recipes")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn()
				.getResolvedException();
		
		assertThat(exception).isExactlyInstanceOf(MissingRequestHeaderException.class);
	}
	
	@Test
	void GivenInvalidJwtToken_WhenTriedForAPICall_ThenResponseIsBadRequest() throws Exception {
		String content = objectMapper.writeValueAsString(TestUtil.getRecipe());
		
		final Exception exception = mockMvc.perform(post("/api/recipes")
				.content(content)
				.header(AUTHORIZATION, BEARER + INVALID_JWT_TOKEN)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isUnauthorized())
				.andReturn()
				.getResolvedException();
		
		assertThat(exception).isExactlyInstanceOf(SignatureException.class);
	}
	
	@Test
	void GivenValidJwtToken_WhenTriedForCreatingRecipeWithInvalidData_ThenResponseIsBadRequest() throws Exception {
		String jwt = getJwtToken();
		Recipe recipe = TestUtil.getRecipe();
		recipe.setName(null);
		String content = objectMapper.writeValueAsString(recipe);
		
		final Exception exception = mockMvc.perform(post("/api/recipes")
				.content(content)
				.header(AUTHORIZATION, BEARER + jwt)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest())
				.andReturn()
				.getResolvedException();
		
		assertThat(exception).isExactlyInstanceOf(MethodArgumentNotValidException.class);
	}
	
	@Test
	void GivenValidJwtToken_WhenTriedForCreatingRecipeWithValidData_ThenResponseIsCreated() throws Exception {
		String jwt = getJwtToken();
		Recipe savedRecipe = deserializeRecipe(createRecipe(jwt));
		Recipe recipe = TestUtil.getRecipe();
		
		TestUtil.verifyResults(savedRecipe, recipe);
	}
	
	@Test
	void GivenValidJwtToken_WhenTriedForCreatingRecipeWithSameName_ThenResponseIsConflict() throws Exception {
		String jwt = getJwtToken();
		createRecipe(jwt);
		
		ResultActions resultActions = createRecipe(jwt);
		
		final Exception exception = resultActions
				.andExpect(status().isConflict())
				.andReturn()
				.getResolvedException();
		
		assertThat(exception).isExactlyInstanceOf(RecipeAlreadyExistsException.class);
	}
	
	@Test
	void GivenValidJwtToken_WhenTriedForGettingNonExistingRecipe_ThenResponseIsNotFound() throws Exception {
		String jwt = getJwtToken();
		Recipe savedRecipe = deserializeRecipe(createRecipe(jwt));
		
		final Exception exception = mockMvc.perform(get(String.format("/api/recipes/%d", savedRecipe.getId() + 1))
				.header(AUTHORIZATION, BEARER + jwt)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andReturn()
				.getResolvedException();
		
		assertThat(exception).isExactlyInstanceOf(RecipeNotFoundException.class);
	}
	
	@Test
	void GivenValidJwtToken_WhenTriedForGettingExistingRecipe_ThenResponseIsOk() throws Exception {
		String jwt = getJwtToken();
		Recipe savedRecipe = deserializeRecipe(createRecipe(jwt));
		
		String json = mockMvc.perform(get(String.format("/api/recipes/%d", savedRecipe.getId()))
				.header(AUTHORIZATION, BEARER + jwt)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();
		
		Recipe recipe = objectMapper.readValue(json, Recipe.class);
		
		TestUtil.verifyResults(savedRecipe, recipe);
	}
	
	@Test
	void GivenValidJwtToken_WhenTriedForGettingNonExistingRecipes_ThenResponseIsNotFound() throws Exception {
		String jwt = getJwtToken();
		
		final Exception exception = mockMvc.perform(get("/api/recipes")
				.header(AUTHORIZATION, BEARER + jwt)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andReturn()
				.getResolvedException();
		
		assertThat(exception).isExactlyInstanceOf(RecipeNotFoundException.class);
	}
	
	@Test
	void GivenValidJwtToken_WhenTriedForGettingAllRecipes_ThenResponseIsOk() throws Exception {
		String jwt = getJwtToken();
		createRecipe(jwt);
		
		mockMvc.perform(get("/api/recipes")
				.header(AUTHORIZATION, BEARER + jwt)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)));
	}
	
	@Test
	void GivenValidJwtToken_WhenTriedForUpdatingNonExistingRecipe_ThenResponseIsNotFound() throws Exception {
		String jwt = getJwtToken();
		Recipe savedRecipe = deserializeRecipe(createRecipe(jwt));
		
		savedRecipe.setServingCapacity(8);
		String content = objectMapper.writeValueAsString(savedRecipe);
		
		final Exception exception = mockMvc.perform(put(String.format("/api/recipes/%d", savedRecipe.getId() + 1))
				.header(AUTHORIZATION, BEARER + jwt)
				.content(content)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andReturn()
				.getResolvedException();
		
		assertThat(exception).isExactlyInstanceOf(RecipeNotFoundException.class);
	}
	
	@Test
	void GivenValidJwtToken_WhenTriedForUpdatingExistingRecipe_ThenResponseIsOk() throws Exception {
		String jwt = getJwtToken();
		Recipe savedRecipe = deserializeRecipe(createRecipe(jwt));
		
		savedRecipe.setServingCapacity(8);
		String content = objectMapper.writeValueAsString(savedRecipe);
		
		ResultActions resultActions = mockMvc.perform(put(String.format("/api/recipes/%d", savedRecipe.getId()))
				.header(AUTHORIZATION, BEARER + jwt)
				.content(content)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
		
		Recipe updatedRecipe = deserializeRecipe(resultActions);
		
		assertEquals(8, updatedRecipe.getServingCapacity());
		
	}
	
	@Test
	void GivenValidJwtToken_WhenTriedForDeletingNonExistingRecipe_ThenResponseIsNotFound() throws Exception {
		String jwt = getJwtToken();
		Recipe savedRecipe = deserializeRecipe(createRecipe(jwt));
		
		final Exception exception = mockMvc.perform(delete(String.format("/api/recipes/%d", savedRecipe.getId() + 1))
				.header(AUTHORIZATION, BEARER + jwt)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andReturn()
				.getResolvedException();
		
		assertThat(exception).isExactlyInstanceOf(RecipeNotFoundException.class);
	}
	
	@Test
	void GivenValidJwtToken_WhenTriedForDeletingExistingRecipe_ThenResponseIsNoContent() throws Exception {
		String jwt = getJwtToken();
		Recipe savedRecipe = deserializeRecipe(createRecipe(jwt));
		
		mockMvc.perform(delete(String.format("/api/recipes/%d", savedRecipe.getId()))
				.header(AUTHORIZATION, BEARER + jwt)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());
	}
	
	private String getJwtToken() throws Exception {
		SignUpRequest signUpRequest = SignUpRequest.builder()
				.username("username")
				.email("user@user.com")
				.password("password")
				.build();

		String content = objectMapper.writeValueAsString(signUpRequest);

		mockMvc.perform(post("/api/auth/signup")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON));

		SignInRequest signInRequest = SignInRequest.builder()
				.username("username")
				.password("password")
				.build();

		content = objectMapper.writeValueAsString(signInRequest);

		String json = mockMvc.perform(post("/api/auth/signin")
				.content(content)
				.contentType(MediaType.APPLICATION_JSON))
				.andReturn()
				.getResponse()
				.getContentAsString();
		
		JwtResponse response = objectMapper.readValue(json, JwtResponse.class);
		
		return response.getJwt();
	}
	
	private ResultActions createRecipe(String jwt) throws Exception {
		Recipe recipe = TestUtil.getRecipe();
		String content = objectMapper.writeValueAsString(recipe);
		
		return mockMvc.perform(post("/api/recipes")
				.content(content)
				.header(AUTHORIZATION, BEARER + jwt)
				.contentType(MediaType.APPLICATION_JSON));
	}
	
	private Recipe deserializeRecipe(ResultActions resultActions) throws Exception {
		String json = resultActions
				.andExpect(status().is2xxSuccessful())
				.andReturn()
				.getResponse()
				.getContentAsString();
		
		Recipe recipe = objectMapper.readValue(json, Recipe.class);
		return recipe;
	}
}
