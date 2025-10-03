package com.sovan.chutesladders.controller.exception;

import com.sovan.chutesladders.exception.GameNotFoundException;
import com.sovan.chutesladders.exception.InconsistentGameException;
import com.sovan.chutesladders.exception.NotYourTurnException;
import com.sovan.chutesladders.exception.PlayerNumbersException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


/**
 * Global exception handler for the application.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles the {@link PlayerNumbersException}.
     *
     * @param ex the exception
     * @return a response entity with a bad request status
     */
    @ExceptionHandler(PlayerNumbersException.class)
    public final ResponseEntity<String> handleMaxPlayerException(PlayerNumbersException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }


    /**
     * Handles the {@link GameNotFoundException}.
     *
     * @param ex the exception
     * @return a response entity with a bad request status
     */
    @ExceptionHandler(GameNotFoundException.class)
    public final ResponseEntity<String> handleNotYourTurnException(GameNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles the {@link InconsistentGameException}.
     *
     * @param ex the exception
     * @return a response entity with a bad request status
     */
    @ExceptionHandler(InconsistentGameException.class)
    public final ResponseEntity<String> handleNotYourTurnException(InconsistentGameException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles the {@link NotYourTurnException}.
     *
     * @param ex the exception
     * @return a response entity with a forbidden status
     */
    @ExceptionHandler(NotYourTurnException.class)
    public final ResponseEntity<String> handleNotYourTurnException(NotYourTurnException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.FORBIDDEN);
    }

    /**
     * Handles all other exceptions.
     *
     * @param ex the exception
     * @return a response entity with an internal server error status
     */
    @ExceptionHandler(Exception.class)
    public final ResponseEntity<String> handleAllExceptions(Exception ex) {
        return new ResponseEntity<>("An internal server error occurred.", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}