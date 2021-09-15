package com.gorkem.recipe.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gorkem.recipe.model.Recipe;
import com.gorkem.recipe.payload.response.MessageResponse;
import com.gorkem.recipe.security.JwtUtil;
import com.gorkem.recipe.service.RecipeService;
import com.gorkem.recipe.service.UserDetailsServiceImpl;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

/**
 * Recipe controller for all CRUD operations
 * 
 * @author gorkemdemiray
 */
@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

	private static final String UNAUTHORIZED_USER = "User is unauthorized!";

	private final RecipeService recipeService;
	private final UserDetailsServiceImpl userDetailsService;
	private final JwtUtil jwtUtil;

	public RecipeController(RecipeService recipeService, UserDetailsServiceImpl userDetailsService, JwtUtil jwtUtil) {
		this.recipeService = recipeService;
		this.userDetailsService = userDetailsService;
		this.jwtUtil = jwtUtil;
	}

	private boolean authenticateUser(final String authHeader) {
		String jwt = jwtUtil.parseJwt(authHeader);
		if (jwt != null) {
			String username = jwtUtil.getUsernameFromToken(jwt);
			UserDetails userDetails = userDetailsService.loadUserByUsername(username);
			return jwtUtil.validateJwtToken(jwt, userDetails);
		}
		return false;
	}

	@PostMapping
	@Operation(summary = "Creates recipe if user is authorized.")
	@ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Creates recipe and returns the object."),
            @ApiResponse(responseCode = "401", description = "Unauthorized user.")
    })
	public ResponseEntity<?> createRecipe(@RequestHeader(value = "Authorization", required = true) String authHeader,
			@Valid @RequestBody Recipe recipe) {
		if (authenticateUser(authHeader)) {
			final Recipe savedRecipe = recipeService.createRecipe(recipe);
			return ResponseEntity.status(HttpStatus.CREATED).body(savedRecipe);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse(UNAUTHORIZED_USER));
		}
	}

	@GetMapping("/{id}")
	@Operation(summary = "Gets recipe with given id if user is authorized.")
	@ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns the recipe object with given id."),
            @ApiResponse(responseCode = "404", description = "Recipe not found."),
            @ApiResponse(responseCode = "401", description = "Unauthorized user.")
    })
	public ResponseEntity<?> getRecipe(@RequestHeader(value = "Authorization", required = true) String authHeader,
			@PathVariable Long id) {
		if (authenticateUser(authHeader)) {
			final Recipe recipe = recipeService.getRecipe(id);
			return ResponseEntity.status(HttpStatus.OK).body(recipe);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse(UNAUTHORIZED_USER));
		}

	}

	@GetMapping
	@Operation(summary = "Gets all recipes if user is authorized.")
	@ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Returns all recipe objects."),
            @ApiResponse(responseCode = "404", description = "Recipe not found."),
            @ApiResponse(responseCode = "401", description = "Unauthorized user.")
    })
	public ResponseEntity<?> getAllRecipes(@RequestHeader(value = "Authorization", required = true) String authHeader) {
		if (authenticateUser(authHeader)) {
			final List<Recipe> recipes = recipeService.getAllRecipes();
			return ResponseEntity.status(HttpStatus.OK).body(recipes);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse(UNAUTHORIZED_USER));
		}
	}

	@PutMapping("/{id}")
	@Operation(summary = "Updates recipe with given id if user is authorized.")
	@ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Updates recipe and returns the object."),
            @ApiResponse(responseCode = "404", description = "Recipe not found."),
            @ApiResponse(responseCode = "409", description = "Recipe already exists."),
            @ApiResponse(responseCode = "401", description = "Unauthorized user.")
    })
	public ResponseEntity<?> updateRecipe(@RequestHeader(value = "Authorization", required = true) String authHeader,
			@PathVariable Long id, @Valid @RequestBody Recipe recipe) {
		if (authenticateUser(authHeader)) {
			final Recipe savedRecipe = recipeService.updateRecipe(id, recipe);
			return ResponseEntity.status(HttpStatus.OK).body(savedRecipe);
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse(UNAUTHORIZED_USER));
		}
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Deletes recipe with given id if user is authorized.")
	@ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Deletes recipe."),
            @ApiResponse(responseCode = "404", description = "Recipe not found."),
            @ApiResponse(responseCode = "401", description = "Unauthorized user.")
    })
	public ResponseEntity<?> deleteRecipe(@RequestHeader(value = "Authorization", required = true) String authHeader,
			@PathVariable Long id) {
		if (authenticateUser(authHeader)) {
			recipeService.deleteRecipe(id);
			return ResponseEntity.status(HttpStatus.NO_CONTENT).body(id.toString());
		} else {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new MessageResponse(UNAUTHORIZED_USER));
		}
	}
}
