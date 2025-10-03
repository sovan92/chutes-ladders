package com.sovan.chutesladders.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sovan.chutesladders.ChutesLaddersApplication;
import com.sovan.chutesladders.model.GameDTO;
import com.sovan.chutesladders.model.PlayerDTO;
import com.sovan.chutesladders.repository.GameRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
import static org.hamcrest.Matchers.containsString;

@SpringBootTest(classes = ChutesLaddersApplication.class)
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class GameIntegrationTest {

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

    @Test
    void testBasicGameCreationAndRetrieval() throws Exception {
        // Step 1: Create a new game
        List<PlayerDTO> players = createPlayerDTOs();

        MvcResult createResult = mockMvc.perform(post("/v1/chutesandladders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(players)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId").exists())
                .andExpect(jsonPath("$.gameState.nextPlayer").value(0))
                .andExpect(jsonPath("$.gameState.winnerDeclared").value(false))
                .andExpect(jsonPath("$.gameState.playerList").isArray())
                .andExpect(jsonPath("$.gameState.playerList.length()").value(2))
                .andReturn();

        String responseContent = createResult.getResponse().getContentAsString();
        GameDTO createdGame = objectMapper.readValue(responseContent, GameDTO.class);

        // Verify game was persisted
        assertTrue(gameRepository.findById(createdGame.getGameId()).isPresent());

        // Verify game state is correct
        assertNotNull(createdGame.getGameId());
        assertEquals(0, createdGame.getGameState().getNextPlayer());
        assertFalse(createdGame.getGameState().isWinnerDeclared());
        assertEquals(2, createdGame.getGameState().getPlayerList().size());
    }

    @Test
    void testCreateGameWithInvalidPlayerCount() throws Exception {
        // Test with no players
        mockMvc.perform(post("/v1/chutesandladders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Arrays.asList())))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Number of players must be between 1 and 10."));

        // Test with too many players
        List<PlayerDTO> tooManyPlayers = createTooManyPlayerDTOs();
        mockMvc.perform(post("/v1/chutesandladders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(tooManyPlayers)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Number of players must be between 1 and 10."));
    }

    @Test
    void testPlayGameWithNonExistentGame() throws Exception {
        GameDTO nonExistentGame = createTestGameDTO();

        mockMvc.perform(put("/v1/chutesandladders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(nonExistentGame))
                .header("X-Player-Name", "Alice"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("was not found")));
    }

    @Test
    void testPlayGameWrongPlayerTurn() throws Exception {
        // Create a game first
        List<PlayerDTO> players = createPlayerDTOs();

        MvcResult createResult = mockMvc.perform(post("/v1/chutesandladders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(players)))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = createResult.getResponse().getContentAsString();
        GameDTO createdGame = objectMapper.readValue(responseContent, GameDTO.class);

        // Try to play with wrong player (Bob when it's Alice's turn)
        mockMvc.perform(put("/v1/chutesandladders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createdGame))
                .header("X-Player-Name", "Bob"))
                .andExpect(status().isForbidden())
                .andExpect(content().string("It is Alice's turn."));
    }

    @Test
    void testGamePersistence() throws Exception {
        // Create a game
        List<PlayerDTO> players = createPlayerDTOs();

        MvcResult createResult = mockMvc.perform(post("/v1/chutesandladders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(players)))
                .andExpect(status().isOk())
                .andReturn();

        String responseContent = createResult.getResponse().getContentAsString();
        GameDTO createdGame = objectMapper.readValue(responseContent, GameDTO.class);

        // Verify the game is actually saved in the database
        assertTrue(gameRepository.findById(createdGame.getGameId()).isPresent());
        assertEquals(1, gameRepository.count());
    }

    private List<PlayerDTO> createPlayerDTOs() {
        PlayerDTO alice = new PlayerDTO();
        alice.setName("Alice");
        alice.setPosition(0);
        alice.setWinner(false);

        PlayerDTO bob = new PlayerDTO();
        bob.setName("Bob");
        bob.setPosition(0);
        bob.setWinner(false);

        return Arrays.asList(alice, bob);
    }

    private List<PlayerDTO> createTooManyPlayerDTOs() {
        return Arrays.asList(
            new PlayerDTO(), new PlayerDTO(), new PlayerDTO(), new PlayerDTO(),
            new PlayerDTO(), new PlayerDTO(), new PlayerDTO(), new PlayerDTO(),
            new PlayerDTO(), new PlayerDTO(), new PlayerDTO() // 11 players
        );
    }

    private GameDTO createTestGameDTO() {
        GameDTO gameDTO = new GameDTO();
        gameDTO.setGameId(java.util.UUID.randomUUID());
        // Add minimal game state for testing
        return gameDTO;
    }
}
