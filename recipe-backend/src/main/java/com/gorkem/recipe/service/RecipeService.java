package com.gorkem.recipe.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.gorkem.recipe.exception.RecipeAlreadyExistsException;
import com.gorkem.recipe.exception.RecipeNotFoundException;
import com.gorkem.recipe.model.Recipe;
import com.gorkem.recipe.repository.RecipeRepository;
import com.gorkem.recipe.util.RecipeUtil;

/**
 * Recipe service for all CRUD operations
 * 
 * @author gorkemdemiray
 */
@Service
public class RecipeService {

	private final RecipeRepository recipeRepository;

	public RecipeService(RecipeRepository recipeRepository) {
		this.recipeRepository = recipeRepository;
	}

	public Recipe createRecipe(final Recipe recipe) {
		if (recipeRepository.existsByName(recipe.getName()))
			throw new RecipeAlreadyExistsException(String.format("Recipe already exists with name: %s", recipe.getName()));
		LocalDateTime localDateTime = LocalDateTime.now();
		recipe.setCreationTime(RecipeUtil.convertDateTimeToString(localDateTime));
		recipe.setLastModified(localDateTime);
		return recipeRepository.save(recipe);
	}

	public Recipe getRecipe(final Long id) {
		return recipeRepository.findById(id)
				.orElseThrow(() -> new RecipeNotFoundException(String.format("Invalid recipe id : %d", id)));
	}
	
	public List<Recipe> getAllRecipes() {
		List<Recipe> recipes = recipeRepository.findAll();
		if (recipes.size() == 0)
			throw new RecipeNotFoundException("No recipes found!");
		return recipes;
	}
	
	public Recipe updateRecipe(final Long id, final Recipe newRecipe) {
		Recipe recipe = getRecipe(id);
		recipe.setName(newRecipe.getName());
		recipe.setVegetarian(newRecipe.getVegetarian());
		recipe.setIngredients(newRecipe.getIngredients());
		recipe.setServingCapacity(newRecipe.getServingCapacity());
		recipe.setCookingInstructions(newRecipe.getCookingInstructions());
		recipe.setLastModified(LocalDateTime.now());
		return recipeRepository.save(recipe);
	}
	
	public void deleteRecipe(final Long id) {
		getRecipe(id);
		recipeRepository.deleteById(id);
	}
}
