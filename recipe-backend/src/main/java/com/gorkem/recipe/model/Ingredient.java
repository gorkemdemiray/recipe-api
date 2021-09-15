package com.gorkem.recipe.model;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Ingredient domain class.
 * 
 * @author gorkemdemiray
 */
@Entity
@Table
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Ingredient {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank(message = "Ingredient name can not be empty!")
	private String name;
	
	@DecimalMin(value = "0.01", message = "Ingredient quantity can not be lower than 0.01!")
	@DecimalMax(value = "1000.00", message = "Ingredient quantity can not be greater than 1000!")
	@Digits(integer = 4, fraction = 2, message = "Ingredient quantity must have 2 fractions!")
	private BigDecimal quantity;
	
}
