package com.gorkem.recipe.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gorkem.recipe.model.Recipe;

/**
 * Recipe repository
 * 
 * @author gorkemdemiray
 */
@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

	Boolean existsByName(String name);
}
