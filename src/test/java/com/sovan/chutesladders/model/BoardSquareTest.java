package com.sovan.chutesladders.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardSquareTest {

    @Test
    void testDefaultConstructor() {
        BoardSquare square = new BoardSquare();

        assertFalse(square.isChute());
        assertFalse(square.isLadder());
        assertEquals(0, square.getNumberSquaresToSkip());
    }

    @Test
    void testParameterizedConstructor() {
        BoardSquare square = new BoardSquare(true, false, 5);

        assertTrue(square.isChute());
        assertFalse(square.isLadder());
        // The getNumberSquaresToSkip() method returns negative for chutes
        assertEquals(-5, square.getNumberSquaresToSkip());
    }

    @Test
    void testGetNumberSquaresToSkip_WhenLadder() {
        BoardSquare ladder = new BoardSquare(false, true, 10);

        assertEquals(10, ladder.getNumberSquaresToSkip());
    }

    @Test
    void testGetNumberSquaresToSkip_WhenChute() {
        BoardSquare chute = new BoardSquare(true, false, 15);

        assertEquals(-15, chute.getNumberSquaresToSkip());
    }

    @Test
    void testGetNumberSquaresToSkip_WhenNormalSquare() {
        BoardSquare normalSquare = new BoardSquare(false, false, 20);

        assertEquals(0, normalSquare.getNumberSquaresToSkip());
    }

    @Test
    void testGetNumberSquaresToSkip_WhenBothChuteAndLadder() {
        // This is an edge case - if both are true, ladder takes precedence
        BoardSquare square = new BoardSquare(true, true, 8);

        assertEquals(8, square.getNumberSquaresToSkip());
    }
}
