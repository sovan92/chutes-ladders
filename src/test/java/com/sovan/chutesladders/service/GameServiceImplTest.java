package com.sovan.chutesladders.service;

import com.sovan.chutesladders.exception.GameNotFoundException;
import com.sovan.chutesladders.exception.InconsistentGameException;
import com.sovan.chutesladders.exception.NotYourTurnException;
import com.sovan.chutesladders.exception.PlayerNumbersException;
import com.sovan.chutesladders.model.*;
import com.sovan.chutesladders.repository.GameRepository;
import com.sovan.chutesladders.utils.DiceUtils;
import com.sovan.chutesladders.utils.GameMapper;
import com.sovan.chutesladders.utils.GameUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameServiceImplTest {

    @Mock
    private BoardService boardService;

    @Mock
    private GameRepository gameRepository;

    @InjectMocks
    private GameServiceImpl gameService;

    private List<PlayerDTO> validPlayerDTOs;
    private Game testGame;
    private GameDTO testGameDTO;
    private UUID gameId;

    @BeforeEach
    void setUp() {
        gameId = UUID.randomUUID();
        validPlayerDTOs = createValidPlayerDTOs();
        testGame = createTestGame();
        testGameDTO = createTestGameDTO();
    }

    @Test
    void testCreateNewGame_ValidPlayers_ShouldSucceed() throws PlayerNumbersException {
        // Arrange
        when(boardService.getMaxPlayers()).thenReturn(10);
        Game savedGame = createTestGame();
        when(gameRepository.save(any(Game.class))).thenReturn(savedGame);

        try (MockedStatic<GameMapper> gameMapperMock = mockStatic(GameMapper.class)) {
            List<Player> players = createTestPlayers();
            gameMapperMock.when(() -> GameMapper.getPlayers(anyList())).thenReturn(players);
            gameMapperMock.when(() -> GameMapper.getGameDto(any(Game.class))).thenReturn(testGameDTO);

            // Act
            GameDTO result = gameService.createNewGame(validPlayerDTOs);

            // Assert
            assertNotNull(result);
            verify(gameRepository).save(any(Game.class));
        }
    }

    @Test
    void testCreateNewGame_TooManyPlayers_ShouldThrowException() {
        // Arrange
        when(boardService.getMaxPlayers()).thenReturn(10);
        List<PlayerDTO> tooManyPlayers = createTooManyPlayerDTOs();

        // Act & Assert
        assertThrows(PlayerNumbersException.class, () -> gameService.createNewGame(tooManyPlayers));
        verify(gameRepository, never()).save(any(Game.class));
    }

    @Test
    void testCreateNewGame_NoPlayers_ShouldThrowException() {
        // Arrange
        when(boardService.getMaxPlayers()).thenReturn(10);
        List<PlayerDTO> noPlayers = Arrays.asList();

        // Act & Assert
        assertThrows(PlayerNumbersException.class, () -> gameService.createNewGame(noPlayers));
        verify(gameRepository, never()).save(any(Game.class));
    }

    @Test
    void testPlayGame_ValidMove_ShouldSucceed() throws Exception {
        // Arrange
        when(gameRepository.findByIdWithPlayersOptimized(gameId)).thenReturn(Optional.of(testGame));
        when(gameRepository.save(any(Game.class))).thenReturn(testGame);

        Board mockBoard = new Board();
        List<BoardSquare> squares = Arrays.asList(
            new BoardSquare(), new BoardSquare(), new BoardSquare()
        );
        mockBoard.setBoardSquarelist(squares);
        when(boardService.getBoard()).thenReturn(mockBoard);

        try (MockedStatic<GameUtils> gameUtilsMock = mockStatic(GameUtils.class);
             MockedStatic<GameMapper> gameMapperMock = mockStatic(GameMapper.class);
             MockedStatic<DiceUtils> diceUtilsMock = mockStatic(DiceUtils.class)) {

            gameUtilsMock.when(() -> GameUtils.isGameEqual(any(Game.class), any(GameDTO.class))).thenReturn(true);
            gameMapperMock.when(() -> GameMapper.getGameDto(any(Game.class))).thenReturn(testGameDTO);
            diceUtilsMock.when(() -> DiceUtils.roll(6)).thenReturn(3);

            // Act
            GameDTO result = gameService.playGame(testGameDTO, "Alice");

            // Assert
            assertNotNull(result);
            verify(gameRepository).findByIdWithPlayersOptimized(gameId);
            verify(gameRepository).save(any(Game.class));
        }
    }

    @Test
    void testPlayGame_GameNotFound_ShouldThrowException() {
        // Arrange
        when(gameRepository.findByIdWithPlayersOptimized(gameId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(GameNotFoundException.class, () -> gameService.playGame(testGameDTO, "Alice"));
        verify(gameRepository, never()).save(any(Game.class));
    }

    @Test
    void testPlayGame_InconsistentGameState_ShouldThrowException() {
        // Arrange
        when(gameRepository.findByIdWithPlayersOptimized(gameId)).thenReturn(Optional.of(testGame));

        try (MockedStatic<GameUtils> gameUtilsMock = mockStatic(GameUtils.class)) {
            gameUtilsMock.when(() -> GameUtils.isGameEqual(any(Game.class), any(GameDTO.class))).thenReturn(false);

            // Act & Assert
            assertThrows(InconsistentGameException.class, () -> gameService.playGame(testGameDTO, "Alice"));
            verify(gameRepository, never()).save(any(Game.class));
        }
    }

    @Test
    void testPlayGame_NotPlayersTurn_ShouldThrowException() {
        // Arrange
        when(gameRepository.findByIdWithPlayersOptimized(gameId)).thenReturn(Optional.of(testGame));

        try (MockedStatic<GameUtils> gameUtilsMock = mockStatic(GameUtils.class)) {
            gameUtilsMock.when(() -> GameUtils.isGameEqual(any(Game.class), any(GameDTO.class))).thenReturn(true);

            // Act & Assert - Bob tries to play when it's Alice's turn
            assertThrows(NotYourTurnException.class, () -> gameService.playGame(testGameDTO, "Bob"));
            verify(gameRepository, never()).save(any(Game.class));
        }
    }

    @Test
    void testGetGameFromRepository_GameExists_ShouldReturnGame() throws GameNotFoundException {
        // Arrange
        when(gameRepository.findByIdWithPlayersOptimized(gameId)).thenReturn(Optional.of(testGame));

        // Act
        Game result = gameService.getGameFromRepository(gameId);

        // Assert
        assertNotNull(result);
        assertEquals(testGame, result);
        verify(gameRepository).findByIdWithPlayersOptimized(gameId);
    }

    @Test
    void testGetGameFromRepository_GameDoesNotExist_ShouldThrowException() {
        // Arrange
        when(gameRepository.findByIdWithPlayersOptimized(gameId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(GameNotFoundException.class, () -> gameService.getGameFromRepository(gameId));
        verify(gameRepository).findByIdWithPlayersOptimized(gameId);
    }

    @Test
    void testUpdateNextPlayer() {
        // Arrange
        Game game = createTestGame();
        int initialNextPlayer = game.getGameState().getNextPlayer();

        // Act
        gameService.updateNextPlayer(game);

        // Assert
        int expectedNextPlayer = (initialNextPlayer + 1) % game.getGameState().getPlayerList().size();
        assertEquals(expectedNextPlayer, game.getGameState().getNextPlayer());
    }

    @Test
    void testUpdateNextPlayer_CyclesBackToFirst() {
        // Arrange
        Game game = createTestGame();
        game.getGameState().setNextPlayer(1); // Set to last player (Bob)

        // Act
        gameService.updateNextPlayer(game);

        // Assert
        assertEquals(0, game.getGameState().getNextPlayer()); // Should cycle back to first player
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

    private List<PlayerDTO> createTooManyPlayerDTOs() {
        List<PlayerDTO> players = Arrays.asList(
            new PlayerDTO(), new PlayerDTO(), new PlayerDTO(), new PlayerDTO(),
            new PlayerDTO(), new PlayerDTO(), new PlayerDTO(), new PlayerDTO(),
            new PlayerDTO(), new PlayerDTO(), new PlayerDTO() // 11 players
        );
        return players;
    }

    private Game createTestGame() {
        Game game = new Game();
        game.setGameId(gameId);

        GameState gameState = new GameState();
        gameState.setNextPlayer(0); // Alice's turn
        gameState.setWinnerDeclared(false);
        gameState.setPlayerList(createTestPlayers());

        game.setGameState(gameState);
        return game;
    }

    private List<Player> createTestPlayers() {
        Player alice = new Player();
        alice.setName("Alice");
        alice.setPosition(0);
        alice.setWinner(false);

        Player bob = new Player();
        bob.setName("Bob");
        bob.setPosition(0);
        bob.setWinner(false);

        return Arrays.asList(alice, bob);
    }

    private GameDTO createTestGameDTO() {
        GameDTO gameDTO = new GameDTO();
        gameDTO.setGameId(gameId);

        GameStateDTO gameStateDTO = new GameStateDTO();
        gameStateDTO.setNextPlayer(0);
        gameStateDTO.setWinnerDeclared(false);
        gameStateDTO.setPlayerList(validPlayerDTOs);

        gameDTO.setGameState(gameStateDTO);
        return gameDTO;
    }
}
