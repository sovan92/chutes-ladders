package com.sovan.chutesladders.exception;

/**
 * Exception thrown when the game state is inconsistent.
 */
public class InconsistentGameException extends Exception {
    /**
     * Constructs a new InconsistentGameException with the specified detail message.
     *
     * @param message the detail message
     */
    public InconsistentGameException(String message) {
        super(message);
    }
}
