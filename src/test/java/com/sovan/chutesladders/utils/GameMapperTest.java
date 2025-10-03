package com.sovan.chutesladders.utils;

import com.sovan.chutesladders.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GameMapperTest {

    private Game testGame;
    private List<PlayerDTO> testPlayerDTOs;

    @BeforeEach
    void setUp() {
        // Setup test data
        testPlayerDTOs = Arrays.asList(
                createPlayerDTO("Alice", 5, false),
                createPlayerDTO("Bob", 10, true)
        );

        testGame = createGame();
    }

    @Test
    void testGetPlayers() {
        List<Player> result = GameMapper.getPlayers(testPlayerDTOs);

        assertEquals(2, result.size());

        Player alice = result.get(0);
        assertEquals("Alice", alice.getName());
        assertEquals(5, alice.getPosition());
        assertFalse(alice.isWinner());

        Player bob = result.get(1);
        assertEquals("Bob", bob.getName());
        assertEquals(10, bob.getPosition());
        assertTrue(bob.isWinner());
    }

    @Test
    void testGetGameDto() {
        GameDTO result = GameMapper.getGameDto(testGame);

        assertNotNull(result);
        assertEquals(testGame.getGameId(), result.getGameId());

        GameStateDTO gameStateDTO = result.getGameState();
        assertNotNull(gameStateDTO);
        assertEquals(1, gameStateDTO.getNextPlayer());
        assertTrue(gameStateDTO.isWinnerDeclared());

        List<PlayerDTO> playerDTOs = gameStateDTO.getPlayerList();
        assertEquals(2, playerDTOs.size());

        assertEquals("Alice", playerDTOs.get(0).getName());
        assertEquals(5, playerDTOs.get(0).getPosition());
        assertFalse(playerDTOs.get(0).isWinner());

        assertEquals("Bob", playerDTOs.get(1).getName());
        assertEquals(10, playerDTOs.get(1).getPosition());
        assertTrue(playerDTOs.get(1).isWinner());
    }

    private PlayerDTO createPlayerDTO(String name, int position, boolean isWinner) {
        PlayerDTO dto = new PlayerDTO();
        dto.setName(name);
        dto.setPosition(position);
        dto.setWinner(isWinner);
        return dto;
    }

    private Game createGame() {
        Game game = new Game();
        game.setGameId(java.util.UUID.randomUUID());

        GameState gameState = new GameState();
        gameState.setNextPlayer(1);
        gameState.setWinnerDeclared(true);

        List<Player> players = Arrays.asList(
                createPlayer("Alice", 5, false),
                createPlayer("Bob", 10, true)
        );
        gameState.setPlayerList(players);

        game.setGameState(gameState);
        return game;
    }

    private Player createPlayer(String name, int position, boolean isWinner) {
        Player player = new Player();
        player.setName(name);
        player.setPosition(position);
        player.setWinner(isWinner);
        return player;
    }
}
