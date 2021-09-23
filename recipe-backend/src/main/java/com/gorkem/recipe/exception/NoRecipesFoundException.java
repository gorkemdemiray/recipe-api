package com.gorkem.recipe.exception;

public class NoRecipesFoundException extends RuntimeException {

    public NoRecipesFoundException(String message) {
        super(message);
    }
}
