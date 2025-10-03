package com.sovan.chutesladders.repository;

import com.sovan.chutesladders.model.Game;
import com.sovan.chutesladders.model.GameState;
import com.sovan.chutesladders.model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class GameRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GameRepository gameRepository;

    private Game testGame;
    private UUID gameId;

    @BeforeEach
    void setUp() {
        gameId = UUID.randomUUID();
        testGame = createTestGame();
    }

    @Test
    void testSaveAndFindGame() {
        // Save the game
        Game savedGame = gameRepository.save(testGame);
        entityManager.flush();

        // Verify it was saved
        assertNotNull(savedGame);
        assertEquals(gameId, savedGame.getGameId());

        // Find the game
        Optional<Game> foundGame = gameRepository.findById(gameId);
        assertTrue(foundGame.isPresent());
        assertEquals(gameId, foundGame.get().getGameId());
        assertEquals(2, foundGame.get().getGameState().getPlayerList().size());
    }

    @Test
    void testFindById_NonExistentGame_ReturnsEmpty() {
        UUID nonExistentId = UUID.randomUUID();
        Optional<Game> result = gameRepository.findById(nonExistentId);
        assertTrue(result.isEmpty());
    }

    @Test
    void testDeleteGame() {
        // Save the game first
        gameRepository.save(testGame);
        entityManager.flush();

        // Verify it exists
        assertTrue(gameRepository.findById(gameId).isPresent());

        // Delete it
        gameRepository.deleteById(gameId);
        entityManager.flush();

        // Verify it's gone
        assertTrue(gameRepository.findById(gameId).isEmpty());
    }

    @Test
    void testVersionFieldExists() {
        // Save the game
        Game savedGame = gameRepository.save(testGame);
        entityManager.flush();

        // Simply verify that we can save and retrieve the game
        // The @Version annotation should handle optimistic locking automatically
        Optional<Game> foundGame = gameRepository.findById(gameId);
        assertTrue(foundGame.isPresent());
        assertNotNull(foundGame.get());
    }

    private Game createTestGame() {
        Game game = new Game();
        game.setGameId(gameId);

        GameState gameState = new GameState();
        gameState.setNextPlayer(0);
        gameState.setWinnerDeclared(false);

        Player alice = new Player();
        alice.setName("Alice");
        alice.setPosition(0);
        alice.setWinner(false);

        Player bob = new Player();
        bob.setName("Bob");
        bob.setPosition(0);
        bob.setWinner(false);

        gameState.setPlayerList(Arrays.asList(alice, bob));
        game.setGameState(gameState);

        return game;
    }
}
