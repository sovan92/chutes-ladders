package com.sovan.chutesladders.exception;

/**
 * Exception thrown when a player tries to play out of turn.
 */
public class NotYourTurnException extends Exception {

    /**
     * Constructs a new NotYourTurnException with the specified detail message.
     *
     * @param msg the detail message
     */
    public NotYourTurnException(String msg) {
        super(msg);
    }

}
