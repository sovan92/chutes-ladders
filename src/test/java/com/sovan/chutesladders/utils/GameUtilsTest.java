package com.sovan.chutesladders.utils;

import com.sovan.chutesladders.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GameUtilsTest {

    private Game testGame;
    private GameDTO testGameDTO;
    private UUID gameId;

    @BeforeEach
    void setUp() {
        gameId = UUID.randomUUID();
        testGame = createTestGame();
        testGameDTO = createTestGameDTO();
    }

    @Test
    void testIsGameEqual_WhenEqual_ShouldReturnTrue() {
        boolean result = GameUtils.isGameEqual(testGame, testGameDTO);
        assertTrue(result);
    }

    @Test
    void testIsGameEqual_WhenDifferentUUID_ShouldReturnFalse() {
        testGameDTO.setGameId(UUID.randomUUID());
        boolean result = GameUtils.isGameEqual(testGame, testGameDTO);
        assertFalse(result);
    }

    @Test
    void testIsGameEqual_WhenDifferentGameState_ShouldReturnFalse() {
        testGameDTO.getGameState().setNextPlayer(2);
        boolean result = GameUtils.isGameEqual(testGame, testGameDTO);
        assertFalse(result);
    }

    @Test
    void testIsUUIDEqual_WhenEqual_ShouldReturnTrue() {
        boolean result = GameUtils.isUUIDEqual(testGame, testGameDTO);
        assertTrue(result);
    }

    @Test
    void testIsUUIDEqual_WhenDifferent_ShouldReturnFalse() {
        testGameDTO.setGameId(UUID.randomUUID());
        boolean result = GameUtils.isUUIDEqual(testGame, testGameDTO);
        assertFalse(result);
    }

    @Test
    void testIsGameStateEqual_WhenEqual_ShouldReturnTrue() {
        GameState gameState = testGame.getGameState();
        GameStateDTO gameStateDTO = testGameDTO.getGameState();

        boolean result = GameUtils.isGameStateEqual(gameState, gameStateDTO);
        assertTrue(result);
    }

    @Test
    void testIsGameStateEqual_WhenDifferentNextPlayer_ShouldReturnFalse() {
        GameState gameState = testGame.getGameState();
        GameStateDTO gameStateDTO = testGameDTO.getGameState();
        gameStateDTO.setNextPlayer(2);

        boolean result = GameUtils.isGameStateEqual(gameState, gameStateDTO);
        assertFalse(result);
    }

    @Test
    void testIsGameStateEqual_WhenDifferentWinnerDeclared_ShouldReturnFalse() {
        GameState gameState = testGame.getGameState();
        GameStateDTO gameStateDTO = testGameDTO.getGameState();
        gameStateDTO.setWinnerDeclared(!gameState.isWinnerDeclared());

        boolean result = GameUtils.isGameStateEqual(gameState, gameStateDTO);
        assertFalse(result);
    }

    @Test
    void testIsPlayerListEqual_WhenEqual_ShouldReturnTrue() {
        GameState gameState = testGame.getGameState();
        GameStateDTO gameStateDTO = testGameDTO.getGameState();

        boolean result = GameUtils.isPlayerListEqual(gameState, gameStateDTO);
        assertTrue(result);
    }

    @Test
    void testIsPlayerListEqual_WhenDifferentPlayerName_ShouldReturnFalse() {
        GameState gameState = testGame.getGameState();
        GameStateDTO gameStateDTO = testGameDTO.getGameState();
        gameStateDTO.getPlayerList().get(0).setName("DifferentName");

        boolean result = GameUtils.isPlayerListEqual(gameState, gameStateDTO);
        assertFalse(result);
    }

    @Test
    void testIsPlayerListEqual_WhenDifferentPlayerPosition_ShouldReturnFalse() {
        GameState gameState = testGame.getGameState();
        GameStateDTO gameStateDTO = testGameDTO.getGameState();
        gameStateDTO.getPlayerList().get(0).setPosition(999);

        boolean result = GameUtils.isPlayerListEqual(gameState, gameStateDTO);
        assertFalse(result);
    }

    private Game createTestGame() {
        Game game = new Game();
        game.setGameId(gameId);

        GameState gameState = new GameState();
        gameState.setNextPlayer(0);
        gameState.setWinnerDeclared(false);

        List<Player> players = Arrays.asList(
                createPlayer("Alice", 5),
                createPlayer("Bob", 10)
        );
        gameState.setPlayerList(players);
        game.setGameState(gameState);

        return game;
    }

    private GameDTO createTestGameDTO() {
        GameDTO gameDTO = new GameDTO();
        gameDTO.setGameId(gameId);

        GameStateDTO gameStateDTO = new GameStateDTO();
        gameStateDTO.setNextPlayer(0);
        gameStateDTO.setWinnerDeclared(false);

        List<PlayerDTO> players = Arrays.asList(
                createPlayerDTO("Alice", 5),
                createPlayerDTO("Bob", 10)
        );
        gameStateDTO.setPlayerList(players);
        gameDTO.setGameState(gameStateDTO);

        return gameDTO;
    }

    private Player createPlayer(String name, int position) {
        Player player = new Player();
        player.setName(name);
        player.setPosition(position);
        player.setWinner(false);
        return player;
    }

    private PlayerDTO createPlayerDTO(String name, int position) {
        PlayerDTO playerDTO = new PlayerDTO();
        playerDTO.setName(name);
        playerDTO.setPosition(position);
        playerDTO.setWinner(false);
        return playerDTO;
    }
}
