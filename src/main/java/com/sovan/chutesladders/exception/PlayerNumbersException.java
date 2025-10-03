package com.sovan.chutesladders.exception;

/**
 * Exception thrown when the number of players is invalid.
 */
public class PlayerNumbersException extends Exception {

    /**
     * Constructs a new PlayerNumbersException with the specified detail message.
     *
     * @param msg the detail message
     */
    public PlayerNumbersException(String msg) {
        super(msg);
    }

}
