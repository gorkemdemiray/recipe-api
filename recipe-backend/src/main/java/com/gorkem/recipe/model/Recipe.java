package com.gorkem.recipe.model;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Recipe domain class.
 * 
 * @author gorkemdemiray
 */
@Entity
@Table
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Recipe {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank(message = "Recipe name can not be empty!")
	private String name;
	
	private String creationTime;
	
	@NotNull(message = "Vegetarian field can not be empty!")
	private Boolean vegetarian;
	
	@Min(value = 1, message = "Recipe can be at least for 1 person!")
	@Max(value = 100, message = "Recipe can not be for more than 100 people!")
	private Integer servingCapacity;
	
	@NotEmpty(message = "Ingredients can not be empty!")
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<Ingredient> ingredients;
	
	@NotBlank(message = "Cooking instructions can not be empty!")
	@Column(columnDefinition = "TEXT")
	private String cookingInstructions;
	
	private LocalDateTime lastModified;
	
}
