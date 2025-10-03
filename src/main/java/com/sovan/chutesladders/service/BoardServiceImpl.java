package com.sovan.chutesladders.service;

import com.sovan.chutesladders.model.Board;
import com.sovan.chutesladders.model.BoardSquare;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

/**
 * Implementation of the {@link BoardService} interface.
 */
@Service("standardChutesLaddersBoardService")
public class BoardServiceImpl implements BoardService {


    private static final int MAX_PLAYERS = 10;
    private static final int NUMBER_OF_SQUARES = 100;

    private Board board;


    /**
     * Gets the special squares on the board (chutes and ladders).
     *
     * @return a map of the special squares
     */
    public Map<Integer, BoardSquare> getSpecialSquares() {
        return Map.ofEntries(
                entry(1, new BoardSquare(false, true, 37)),
                entry(4, new BoardSquare(false, true, 10)),
                entry(9, new BoardSquare(false, true, 22)),
                entry(16, new BoardSquare(true, false, 10)),
                entry(21, new BoardSquare(false, true, 21)),
                entry(28, new BoardSquare(false, true, 56)),
                entry(36, new BoardSquare(false, true, 8)),
                entry(47, new BoardSquare(true, false, 21)),
                entry(49, new BoardSquare(true, false, 38)),
                entry(51, new BoardSquare(false, true, 16)),
                entry(56, new BoardSquare(true, false, 3)),
                entry(62, new BoardSquare(true, false, 43)),
                entry(64, new BoardSquare(true, false, 4)),
                entry(71, new BoardSquare(false, true, 20)),
                entry(80, new BoardSquare(false, true, 20)),
                entry(87, new BoardSquare(true, false, 63)),
                entry(93, new BoardSquare(true, false, 20)),
                entry(95, new BoardSquare(true, false, 20)),
                entry(98, new BoardSquare(true, false, 20))
        );
    }


    /**
     * Sets up the game board.
     */
    @Override
    public void setUp() {
        this.board = new Board();
        Map<Integer, BoardSquare> specialSquares = getSpecialSquares();
        List<BoardSquare> squares = java.util.stream.IntStream.rangeClosed(1, NUMBER_OF_SQUARES)
                .mapToObj(i -> specialSquares.getOrDefault(i, new BoardSquare()))
                .collect(java.util.stream.Collectors.toCollection(java.util.ArrayList::new));
        this.board.setBoardSquarelist(squares);
    }


    /**
     * Gets the maximum number of players allowed on the board.
     *
     * @return the maximum number of players
     */
    @Override
    public int getMaxPlayers() {
        return MAX_PLAYERS;
    }


    /**
     * Gets the game board.
     *
     * @return the game board
     */
    @Override
    public Board getBoard() {
        return board;
    }


}
