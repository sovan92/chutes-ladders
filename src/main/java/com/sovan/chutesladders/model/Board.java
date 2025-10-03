package com.sovan.chutesladders.model;

import lombok.Data;

import java.util.List;

/**
 * Represents the game board.
 */
@Data
public class Board {
    /**
     * The list of squares on the board.
     */
    private List<BoardSquare> boardSquarelist = null;
}
