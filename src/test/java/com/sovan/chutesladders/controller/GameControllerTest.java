package com.sovan.chutesladders.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sovan.chutesladders.exception.GameNotFoundException;
import com.sovan.chutesladders.exception.InconsistentGameException;
import com.sovan.chutesladders.exception.NotYourTurnException;
import com.sovan.chutesladders.exception.PlayerNumbersException;
import com.sovan.chutesladders.model.GameDTO;
import com.sovan.chutesladders.model.GameStateDTO;
import com.sovan.chutesladders.model.PlayerDTO;
import com.sovan.chutesladders.service.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GameController.class)
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GameService gameService;

    @Autowired
    private ObjectMapper objectMapper;

    private List<PlayerDTO> validPlayerDTOs;
    private GameDTO testGameDTO;

    @BeforeEach
    void setUp() {
        validPlayerDTOs = createValidPlayerDTOs();
        testGameDTO = createTestGameDTO();
    }

    @Test
    void testGameSetUp_ValidPlayers_ShouldReturn200() throws Exception {
        // Arrange
        when(gameService.createNewGame(anyList())).thenReturn(testGameDTO);

        // Act & Assert
        mockMvc.perform(post("/v1/chutesandladders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validPlayerDTOs)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.gameId").exists())
                .andExpect(jsonPath("$.gameState").exists())
                .andExpect(jsonPath("$.gameState.nextPlayer").value(0))
                .andExpect(jsonPath("$.gameState.winnerDeclared").value(false));
    }

    @Test
    void testGameSetUp_InvalidPlayerCount_ShouldReturn400() throws Exception {
        // Arrange
        when(gameService.createNewGame(anyList()))
                .thenThrow(new PlayerNumbersException("Invalid number of players"));

        // Act & Assert
        mockMvc.perform(post("/v1/chutesandladders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validPlayerDTOs)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid number of players"));
    }

    @Test
    void testUpdateGame_ValidMove_ShouldReturn200() throws Exception {
        // Arrange
        GameDTO updatedGame = createTestGameDTO();
        updatedGame.getGameState().setNextPlayer(1);

        when(gameService.playGame(any(GameDTO.class), eq("Alice")))
                .thenReturn(updatedGame);

        // Act & Assert
        mockMvc.perform(put("/v1/chutesandladders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testGameDTO))
                .header("X-Player-Name", "Alice"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.gameId").exists())
                .andExpect(jsonPath("$.gameState.nextPlayer").value(1));
    }

    @Test
    void testUpdateGame_GameNotFound_ShouldReturn400() throws Exception {
        // Arrange
        when(gameService.playGame(any(GameDTO.class), eq("Alice")))
                .thenThrow(new GameNotFoundException("Game not found"));

        // Act & Assert
        mockMvc.perform(put("/v1/chutesandladders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testGameDTO))
                .header("X-Player-Name", "Alice"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Game not found"));
    }

    @Test
    void testUpdateGame_NotYourTurn_ShouldReturn403() throws Exception {
        // Arrange
        when(gameService.playGame(any(GameDTO.class), eq("Bob")))
                .thenThrow(new NotYourTurnException("It is Alice's turn"));

        // Act & Assert
        mockMvc.perform(put("/v1/chutesandladders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testGameDTO))
                .header("X-Player-Name", "Bob"))
                .andExpect(status().isForbidden())
                .andExpect(content().string("It is Alice's turn"));
    }

    @Test
    void testUpdateGame_InconsistentGameState_ShouldReturn400() throws Exception {
        // Arrange
        when(gameService.playGame(any(GameDTO.class), eq("Alice")))
                .thenThrow(new InconsistentGameException("Game state inconsistent"));

        // Act & Assert
        mockMvc.perform(put("/v1/chutesandladders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testGameDTO))
                .header("X-Player-Name", "Alice"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Game state inconsistent"));
    }

    // Helper methods
    private List<PlayerDTO> createValidPlayerDTOs() {
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

    private GameDTO createTestGameDTO() {
        GameDTO gameDTO = new GameDTO();
        gameDTO.setGameId(UUID.randomUUID());

        GameStateDTO gameStateDTO = new GameStateDTO();
        gameStateDTO.setNextPlayer(0);
        gameStateDTO.setWinnerDeclared(false);
        gameStateDTO.setPlayerList(validPlayerDTOs);

        gameDTO.setGameState(gameStateDTO);
        return gameDTO;
    }
}
