package com.sovan.chutesladders.exception;

/**
 * Exception thrown when a game is not found.
 */
public class GameNotFoundException extends Exception {
    /**
     * Constructs a new GameNotFoundException with the specified detail message.
     *
     * @param message the detail message
     */
    public GameNotFoundException(String message) {
        super(message);
    }
}
