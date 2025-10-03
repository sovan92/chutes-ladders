package com.sovan.chutesladders.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sovan.chutesladders.ChutesLaddersApplication;
import com.sovan.chutesladders.model.GameDTO;
import com.sovan.chutesladders.model.PlayerDTO;
import com.sovan.chutesladders.repository.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration test that runs a complete Chutes and Ladders game with 3 players
 * (Alice, Bob, and Joan) until someone wins.
 */
@SpringBootTest(classes = ChutesLaddersApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@ActiveProfiles("test")
@Transactional
class CompleteGamePlayTest {

    private static final Logger logger = LoggerFactory.getLogger(CompleteGamePlayTest.class);

    private static final int MAX_MOVES = 1000; // Safety limit to prevent infinite loops
    private static final String[] PLAYER_NAMES = {"Alice", "Bob", "Joan"};

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private GameRepository gameRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        gameRepository.deleteAll();
    }

    /**
     * Test that runs a complete game with Alice, Bob, and Joan until someone wins.
     * This test simulates real gameplay by:
     * 1. Creating a game with 3 players
     * 2. Taking turns for each player
     * 3. Continuing until a winner is declared
     * 4. Verifying the winner and final game state
     */
    @Test
    void testCompleteGameWithThreePlayersUntilWinner() throws Exception {
        logger.info("🎮 Starting complete Chutes and Ladders game with Alice, Bob, and Joan");

        // Step 1: Create a new game with 3 players
        List<PlayerDTO> players = createThreePlayerGame();

        MvcResult createResult = mockMvc.perform(post("/v1/chutesandladders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(players)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId").exists())
                .andExpect(jsonPath("$.gameState.nextPlayer").value(0))
                .andExpect(jsonPath("$.gameState.winnerDeclared").value(false))
                .andExpect(jsonPath("$.gameState.playerList").isArray())
                .andExpect(jsonPath("$.gameState.playerList.length()").value(3))
                .andReturn();

        String responseContent = createResult.getResponse().getContentAsString();
        GameDTO currentGame = objectMapper.readValue(responseContent, GameDTO.class);

        logger.info("Game created with ID: {}", currentGame.getGameId());
        logger.info("Players: Alice (position 0), Bob (position 0), Joan (position 0)");

        // Verify initial game state
        assertNotNull(currentGame.getGameId());
        assertEquals(3, currentGame.getGameState().getPlayerList().size());
        assertFalse(currentGame.getGameState().isWinnerDeclared());

        // Verify game was persisted
        assertTrue(gameRepository.findById(currentGame.getGameId()).isPresent());

        // Step 2: Play the game until someone wins
        int moveCount = 0;
        String winner = null;

        while (!currentGame.getGameState().isWinnerDeclared() && moveCount < MAX_MOVES) {
            int currentPlayerIndex = currentGame.getGameState().getNextPlayer();
            String currentPlayerName = PLAYER_NAMES[currentPlayerIndex];

            // Log current player positions before the move
            logPlayerPositions(currentGame, moveCount + 1, currentPlayerName);

            // Make a move for the current player
            MvcResult moveResult = mockMvc.perform(put("/v1/chutesandladders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(currentGame))
                    .header("X-Player-Name", currentPlayerName))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.gameId").value(currentGame.getGameId().toString()))
                    .andReturn();

            String moveResponseContent = moveResult.getResponse().getContentAsString();
            currentGame = objectMapper.readValue(moveResponseContent, GameDTO.class);

            moveCount++;

            // Check if we have a winner
            if (currentGame.getGameState().isWinnerDeclared()) {
                winner = findWinner(currentGame);
                logger.info(" GAME OVER! Winner declared after {} moves!", moveCount);
                break;
            }

            // Add a small delay for readability in logs (optional)
            if (moveCount % 10 == 0) {
                logger.info("Move count: {}", moveCount);
            }
        }

        // Step 3: Verify the final game state and winner
        assertNotNull(winner, "A winner should have been declared");
        assertTrue(currentGame.getGameState().isWinnerDeclared(), "Winner should be declared");
        assertTrue(Arrays.asList(PLAYER_NAMES).contains(winner), "Winner should be one of our players");
        assertTrue(moveCount < MAX_MOVES, "Game should not exceed maximum moves");

        // Log final results
        logger.info(" FINAL RESULTS:");
        logger.info(" Winner: {}", winner);
        logger.info("Total moves played: {}", moveCount);
        logPlayerPositions(currentGame, moveCount, "FINAL");

        // Verify winner has reached a winning position
        PlayerDTO winningPlayer = currentGame.getGameState().getPlayerList().stream()
                .filter(PlayerDTO::isWinner)
                .findFirst()
                .orElse(null);

        assertNotNull(winningPlayer, "There should be a winning player marked as winner");
        assertEquals(winner, winningPlayer.getName(), "Winner name should match the player marked as winner");

        logger.info(" Game completed successfully! {} won the game!", winner);
    }

    /**
     * Test edge case: Ensure the game doesn't get stuck in infinite loops
     */
    @Test
    void testGameCompletesWithinReasonableTime() throws Exception {
        logger.info("Testing game completion within reasonable time limits");

        List<PlayerDTO> players = createThreePlayerGame();

        MvcResult createResult = mockMvc.perform(post("/v1/chutesandladders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(players)))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = createResult.getResponse().getContentAsString();
        GameDTO currentGame = objectMapper.readValue(responseContent, GameDTO.class);

        int moveCount = 0;
        long startTime = System.currentTimeMillis();

        while (!currentGame.getGameState().isWinnerDeclared() && moveCount < MAX_MOVES) {
            int currentPlayerIndex = currentGame.getGameState().getNextPlayer();
            String currentPlayerName = PLAYER_NAMES[currentPlayerIndex];

            MvcResult moveResult = mockMvc.perform(put("/v1/chutesandladders")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(currentGame))
                    .header("X-Player-Name", currentPlayerName))
                    .andExpect(status().isOk())
                    .andReturn();

            String moveResponseContent = moveResult.getResponse().getContentAsString();
            currentGame = objectMapper.readValue(moveResponseContent, GameDTO.class);
            moveCount++;
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        assertTrue(currentGame.getGameState().isWinnerDeclared(), "Game should complete with a winner");
        assertTrue(moveCount < MAX_MOVES, "Game should complete within reasonable number of moves");
        assertTrue(duration < 30000, "Game should complete within 30 seconds"); // 30 second timeout

        logger.info("⚡ Game completed in {} ms with {} moves", duration, moveCount);
    }

    /**
     * Creates a list of 3 PlayerDTO objects for Alice, Bob, and Joan.
     */
    private List<PlayerDTO> createThreePlayerGame() {
        PlayerDTO alice = new PlayerDTO();
        alice.setName("Alice");
        alice.setPosition(0);
        alice.setWinner(false);

        PlayerDTO bob = new PlayerDTO();
        bob.setName("Bob");
        bob.setPosition(0);
        bob.setWinner(false);

        PlayerDTO joan = new PlayerDTO();
        joan.setName("Joan");
        joan.setPosition(0);
        joan.setWinner(false);

        return Arrays.asList(alice, bob, joan);
    }

    /**
     * Finds and returns the name of the winner from the current game state.
     */
    private String findWinner(GameDTO game) {
        return game.getGameState().getPlayerList().stream()
                .filter(PlayerDTO::isWinner)
                .map(PlayerDTO::getName)
                .findFirst()
                .orElse(null);
    }

    /**
     * Logs the current positions of all players for debugging and tracking game progress.
     */
    private void logPlayerPositions(GameDTO game, int moveNumber, String currentPlayer) {
        StringBuilder positions = new StringBuilder();
        positions.append(String.format(" Move %d (%s's turn): ", moveNumber, currentPlayer));

        for (PlayerDTO player : game.getGameState().getPlayerList()) {
            positions.append(String.format("%s(pos:%d%s) ",
                    player.getName(),
                    player.getPosition(),
                    player.isWinner() ? "Winner" : "Lost"));
        }

        logger.info(positions.toString());
    }
}
