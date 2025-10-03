package com.sovan.chutesladders.model;

import lombok.Data;

/**
 * Represents a single square on the game board.
 */
@Data
public class BoardSquare {

    /**
     * Whether the square is a chute.
     */
    private boolean isChute;
    /**
     * Whether the square is a ladder.
     */
    private boolean isLadder;
    /**
     * The number of squares to skip forward or backward.
     */
    private int numberSquaresToSkip;


    /**
     * Constructs a new, empty board square.
     */
    public BoardSquare() {
        this(false, false, 0);
    }

    /**
     * Constructs a new board square with the given properties.
     *
     * @param isChute             whether the square is a chute
     * @param isLadder            whether the square is a ladder
     * @param numberSquaresToSkip the number of squares to skip
     */
    public BoardSquare(boolean isChute, boolean isLadder, int numberSquaresToSkip) {
        this.isChute = isChute;
        this.isLadder = isLadder;
        this.numberSquaresToSkip = numberSquaresToSkip;
    }

    /**
     * Gets the number of squares to skip.
     *
     * @return the number of squares to skip (positive for a ladder, negative for a chute)
     */
    public int getNumberSquaresToSkip() {
        if (isLadder) {
            return numberSquaresToSkip;
        } else if (isChute) {
            return numberSquaresToSkip * -1;
        } else {
            return 0;
        }
    }
}
