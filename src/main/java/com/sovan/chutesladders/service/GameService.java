package com.sovan.chutesladders.service;


import com.sovan.chutesladders.exception.GameNotFoundException;
import com.sovan.chutesladders.exception.InconsistentGameException;
import com.sovan.chutesladders.exception.NotYourTurnException;
import com.sovan.chutesladders.exception.PlayerNumbersException;
import com.sovan.chutesladders.model.GameDTO;
import com.sovan.chutesladders.model.PlayerDTO;

import java.util.List;

/**
 * Service for managing games.
 */
public interface GameService {

    /**
     * Creates a new game with the given players.
     *
     * @param playerList the list of players to add to the game
     * @return the created game
     * @throws PlayerNumbersException if the number of players is invalid
     */
    public GameDTO createNewGame(List<PlayerDTO> playerList) throws PlayerNumbersException;

    /**
     * Plays a turn in the game for the given player.
     *
     * @param game       the current state of the game
     * @param playerName the name of the player whose turn it is
     * @return the updated game state
     * @throws PlayerNumbersException    if the number of players is invalid
     * @throws NotYourTurnException      if it is not the player's turn
     * @throws GameNotFoundException     if the game is not found
     * @throws InconsistentGameException if the game state is inconsistent
     */
    public GameDTO playGame(GameDTO game, String playerName) throws PlayerNumbersException, NotYourTurnException, GameNotFoundException, InconsistentGameException;
}
