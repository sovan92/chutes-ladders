package com.sovan.chutesladders.service;

import com.sovan.chutesladders.model.Board;

/**
 * Service for managing the game board.
 */
public interface BoardService {

    /**
     * Sets up the game board.
     */
    public void setUp();

    /**
     * Gets the maximum number of players allowed on the board.
     *
     * @return the maximum number of players
     */
    public int getMaxPlayers();

    /**
     * Gets the game board.
     *
     * @return the game board
     */
    public Board getBoard();
}
