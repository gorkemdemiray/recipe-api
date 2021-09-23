package com.gorkem.recipe.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import javax.validation.ConstraintViolationException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import com.gorkem.recipe.exception.NoRecipesFoundException;
import com.gorkem.recipe.exception.RecipeAlreadyExistsException;
import com.gorkem.recipe.exception.RecipeNotFoundException;
import com.gorkem.recipe.model.Recipe;
import com.gorkem.recipe.util.TestUtil;

/**
 * Recipe service integration test class.
 * 
 * @author gorkemdemiray
 */
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class RecipeServiceTest {
	
	@Autowired
	private RecipeService recipeService;
	
	@Test
	void GivenInvalidData_WhenTriedForCreatingRecipe_ThenResponseIsException() throws Exception {
		Recipe recipe = TestUtil.getRecipe();
		recipe.setName(null);
		assertThrows(ConstraintViolationException.class, () -> {
			recipeService.createRecipe(recipe);
		});
	}
	
	@Test
	void GivenValidData_WhenTriedForCreatingRecipe_ThenResponseIsOk() throws Exception {
		Recipe recipe = TestUtil.getRecipe();
		Recipe savedRecipe = recipeService.createRecipe(recipe);
		TestUtil.verifyResults(savedRecipe, recipe);
	}
	
	@Test
	void GivenValidData_WhenTriedForCreatingRecipeWithSameName_ThenResponseIsException() throws Exception {
		Recipe recipe = TestUtil.getRecipe();
		recipeService.createRecipe(recipe);
		assertThrows(RecipeAlreadyExistsException.class, () -> {
			recipeService.createRecipe(recipe);
		});
	}
	
	@Test
	void GivenValidData_WhenTriedForGettingNonExistingRecipe_ThenResponseIsException() throws Exception {
		assertThrows(RecipeNotFoundException.class, () -> {
			recipeService.getRecipe(5L);
		});
	}
	
	@Test
	void GivenValidData_WhenTriedForGettingExistingRecipe_ThenResponseIsOk() throws Exception {
		Recipe recipe = TestUtil.getRecipe();
		Recipe savedRecipe = recipeService.getRecipe(recipeService.createRecipe(recipe).getId());
		TestUtil.verifyResults(savedRecipe, recipe);
	}
	
	@Test
	void GivenValidData_WhenTriedForGettingNonExistingRecipes_ThenResponseIsException() throws Exception {
		assertThrows(NoRecipesFoundException.class, () -> {
			recipeService.getAllRecipes();
		});
	}
	
	@Test
	void GivenValidData_WhenTriedForGettingExistingAllRecipes_ThenResponseIsOk() throws Exception {
		Recipe recipe = TestUtil.getRecipe();
		recipeService.createRecipe(recipe);
		List<Recipe> recipes = recipeService.getAllRecipes();
		TestUtil.verifyResults(recipes.get(0), recipe);
	}
	
	@Test
	void GivenValidData_WhenTriedForUpdatingNonExistingRecipe_ThenResponseIsException() throws Exception {
		Recipe recipe = TestUtil.getRecipe();
		Recipe savedRecipe = recipeService.createRecipe(recipe);
		assertThrows(RecipeNotFoundException.class, () -> {
			recipeService.updateRecipe(savedRecipe.getId() + 1, recipe);
		});
	}
	
	@Test
	void GivenValidData_WhenTriedForUpdatingExistingRecipe_ThenResponseIsOk() throws Exception {
		Recipe recipe = TestUtil.getRecipe();
		Recipe savedRecipe = recipeService.createRecipe(recipe);
		savedRecipe.setServingCapacity(8);
		Recipe updatedRecipe = recipeService.updateRecipe(savedRecipe.getId(), savedRecipe);
		TestUtil.verifyResults(updatedRecipe, savedRecipe);
	}
	
	@Test
	void GivenValidData_WhenTriedForDeletingNonExistingRecipe_ThenResponseIsException() throws Exception {
		Recipe recipe = TestUtil.getRecipe();
		Recipe savedRecipe = recipeService.createRecipe(recipe);
		assertThrows(RecipeNotFoundException.class, () -> {
			recipeService.deleteRecipe(savedRecipe.getId() + 1);
		});
	}
	
	@Test
	void GivenValidData_WhenTriedForDeletingExistingRecipe_ThenResponseIsOk() throws Exception {
		Recipe recipe = TestUtil.getRecipe();
		Recipe savedRecipe = recipeService.createRecipe(recipe);
		recipeService.deleteRecipe(savedRecipe.getId());
	}

}
