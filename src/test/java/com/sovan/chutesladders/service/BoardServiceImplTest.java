package com.sovan.chutesladders.service;

import com.sovan.chutesladders.model.Board;
import com.sovan.chutesladders.model.BoardSquare;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class BoardServiceImplTest {

    private BoardServiceImpl boardService;

    @BeforeEach
    void setUp() {
        boardService = new BoardServiceImpl();
    }

    @Test
    void testGetSpecialSquares() {
        Map<Integer, BoardSquare> specialSquares = boardService.getSpecialSquares();

        assertNotNull(specialSquares);
        assertFalse(specialSquares.isEmpty());

        // Test some specific chutes and ladders
        assertTrue(specialSquares.containsKey(1)); // Ladder at position 1
        assertTrue(specialSquares.containsKey(16)); // Chute at position 16
        assertTrue(specialSquares.containsKey(87)); // Chute at position 87

        // Verify ladder properties
        BoardSquare ladder = specialSquares.get(1);
        assertFalse(ladder.isChute());
        assertTrue(ladder.isLadder());
        assertEquals(37, ladder.getNumberSquaresToSkip());

        // Verify chute properties
        BoardSquare chute = specialSquares.get(16);
        assertTrue(chute.isChute());
        assertFalse(chute.isLadder());
        assertEquals(-10, chute.getNumberSquaresToSkip()); // Should be negative for chutes
    }

    @Test
    void testSetUp() {
        boardService.setUp();
        Board board = boardService.getBoard();

        assertNotNull(board);
        assertNotNull(board.getBoardSquarelist());
        assertEquals(100, board.getBoardSquarelist().size());

        // Verify that special squares are correctly placed
        BoardSquare square1 = board.getBoardSquarelist().get(0); // Position 1 (index 0)
        assertTrue(square1.isLadder());
        assertEquals(37, square1.getNumberSquaresToSkip());

        // Verify normal squares
        BoardSquare normalSquare = board.getBoardSquarelist().get(1); // Position 2 (index 1)
        assertFalse(normalSquare.isChute());
        assertFalse(normalSquare.isLadder());
        assertEquals(0, normalSquare.getNumberSquaresToSkip());
    }

    @Test
    void testGetMaxPlayers() {
        assertEquals(10, boardService.getMaxPlayers());
    }

    @Test
    void testGetBoard_BeforeSetUp() {
        Board board = boardService.getBoard();
        assertNull(board);
    }

    @Test
    void testGetBoard_AfterSetUp() {
        boardService.setUp();
        Board board = boardService.getBoard();

        assertNotNull(board);
        assertNotNull(board.getBoardSquarelist());
        assertEquals(100, board.getBoardSquarelist().size());
    }
}
