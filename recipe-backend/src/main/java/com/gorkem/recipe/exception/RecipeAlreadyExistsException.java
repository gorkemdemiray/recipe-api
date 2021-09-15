package com.gorkem.recipe.exception;

public class RecipeAlreadyExistsException extends RuntimeException {

    public RecipeAlreadyExistsException(String message) {
        super(message);
    }
}
