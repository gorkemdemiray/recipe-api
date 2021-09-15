package com.gorkem.recipe.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for recipe operations.
 * 
 * @author gorkemdemiray
 */
public class RecipeUtil {
	
	private static final String DATETIME_PATTERN = "dd-MM-yyyy HH:mm";
	
	public static String convertDateTimeToString(LocalDateTime localDateTime) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATETIME_PATTERN);
		return formatter.format(localDateTime);
	}
}
