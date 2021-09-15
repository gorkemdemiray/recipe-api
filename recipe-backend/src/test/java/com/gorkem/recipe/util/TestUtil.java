package com.gorkem.recipe.util;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import java.util.Arrays;

import com.gorkem.recipe.model.Ingredient;
import com.gorkem.recipe.model.Recipe;

/**
 * Test utility class for recipe operations.
 * 
 * @author gorkemdemiray
 */
public class TestUtil {
	
	public static Recipe getRecipe() {
		Recipe recipe = Recipe.builder()
				.name("Meal-prep Pesto Chicken & Veggies")
				.vegetarian(true)
				.servingCapacity(4)
				.ingredients(Arrays.asList(Ingredient.builder().name("boneless, skinless chicken thighs, sliced").quantity(new BigDecimal(4)).build(),
						Ingredient.builder().name("lb green beans").quantity(new BigDecimal(455)).build(),
						Ingredient.builder().name("cherry tomato").quantity(new BigDecimal(400)).build(),
						Ingredient.builder().name("basil pesto").quantity(new BigDecimal(115)).build()))
				.cookingInstructions("In a large pan, heat olive oil and add chicken thighs.\n"
						+ "Season with salt and pepper. When the chicken is completely cooked through, remove from pan.\n"
						+ "Slice into strips, and set aside.\n"
						+ "Add green beans and cook until crisp tender.\n"
						+ "Return the chicken strips to the pan, then add tomatoes and pesto. Stir until fully incorporated.\n"
						+ "Serve immediately or divide into 4 food storage containers and store in the refrigerator. Can be kept refrigerated for up to 4 days.")
				.build();
		
		return recipe;
	}

	public static void verifyResults(Recipe savedRecipe, Recipe recipe) {
		assertEquals(savedRecipe.getName(), recipe.getName());
		assertEquals(savedRecipe.getVegetarian(), recipe.getVegetarian());
		assertEquals(savedRecipe.getServingCapacity(), recipe.getServingCapacity());
		assertEquals(savedRecipe.getIngredients().size(), recipe.getIngredients().size());
		assertEquals(savedRecipe.getCookingInstructions(), recipe.getCookingInstructions());
	}
	
}
