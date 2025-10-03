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
import jakarta.persistence.OptimisticLockException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Implementation of the {@link GameService} interface.
 */
@Service
@Slf4j
public class GameServiceImpl implements GameService {

    private final BoardService boardService;
    private final GameRepository gameRepository;

    /**
     * Constructs a new GameServiceImpl with the given dependencies.
     *
     * @param boardService   the board service
     * @param gameRepository the game repository
     */
    public GameServiceImpl(@Qualifier("standardChutesLaddersBoardService") BoardService boardService, GameRepository gameRepository) {
        this.boardService = boardService;
        this.boardService.setUp();
        this.gameRepository = gameRepository;
    }

    /**
     * Creates a new game with the given players.
     *
     * @param players the list of players to add to the game
     * @return the created game
     * @throws PlayerNumbersException if the number of players is invalid
     */
    @Override
    public GameDTO createNewGame(List<PlayerDTO> players) throws PlayerNumbersException {
        validatePlayerCount(players.size());
        Game game = new Game();
        game.setGameId(UUID.randomUUID());
        game.setGameState(initGameState(GameMapper.getPlayers(players)));
        return GameMapper.getGameDto(gameRepository.save(game));
    }

    /**
     * Plays a turn in the game for the given player.
     *
     * @param gameDTO    the current state of the game
     * @param playerName the name of the player whose turn it is
     * @return the updated game state
     * @throws GameNotFoundException     if the game is not found
     * @throws NotYourTurnException      if it is not the player's turn
     * @throws InconsistentGameException if the game state is inconsistent
     */
    @Override
    @Transactional(rollbackOn = {GameNotFoundException.class, NotYourTurnException.class, InconsistentGameException.class, OptimisticLockException.class})
    public GameDTO playGame(GameDTO gameDTO, String playerName) throws GameNotFoundException, NotYourTurnException, InconsistentGameException {
        Game dbGame = getGameFromRepository(gameDTO.getGameId());

        if (!GameUtils.isGameEqual(dbGame, gameDTO)) {
            throw new InconsistentGameException("The provided game state does not match the server's state.");
        }

        validatePlayerTurn(dbGame, playerName);
        processPlayerMove(dbGame);
        updateNextPlayer(dbGame);

        return GameMapper.getGameDto(gameRepository.save(dbGame));
    }

    /**
     * Processes a player's move, including rolling the dice and updating the player's position.
     *
     * @param game the game to process the move for
     */
    private void processPlayerMove(Game game) {
        GameState gameState = game.getGameState();
        Player currentPlayer = gameState.getPlayerList().get(gameState.getNextPlayer());
        int roll = DiceUtils.roll(6);

        int currentPosition = currentPlayer.getPosition();
        int boardSize = boardService.getBoard().getBoardSquarelist().size();
        int nextPosition = currentPosition + roll;

        // Ensure we don't go beyond the board
        if (nextPosition >= boardSize) {
            nextPosition = boardSize - 1; // Move to the last square (winning square)
            gameState.setWinnerDeclared(true);
            currentPlayer.setWinner(true);
        } else if (nextPosition > 0 && nextPosition < boardSize) {
            // Only check for chutes/ladders if we're on a valid board position
            BoardSquare nextSquare = boardService.getBoard().getBoardSquarelist().get(nextPosition - 1);
            nextPosition += nextSquare.getNumberSquaresToSkip();

            // Check again if we've now reached or passed the end after chute/ladder
            if (nextPosition >= boardSize - 1) {
                nextPosition = boardSize - 1;
                gameState.setWinnerDeclared(true);
                currentPlayer.setWinner(true);
            }
        }

        currentPlayer.setPosition(nextPosition);
    }

    /**
     * Validates that it is the correct player's turn.
     *
     * @param game       the game to validate the turn for
     * @param playerName the name of the player
     * @throws NotYourTurnException if it is not the player's turn
     */
    private void validatePlayerTurn(Game game, String playerName) throws NotYourTurnException {
        GameState state = game.getGameState();
        Player playerToPlay = state.getPlayerList().get(state.getNextPlayer());
        if (!playerToPlay.getName().equals(playerName)) {
            throw new NotYourTurnException("It is " + playerToPlay.getName() + "'s turn.");
        }
    }

    /**
     * Validates that the number of players is within the allowed range.
     *
     * @param playerCount the number of players
     * @throws PlayerNumbersException if the number of players is invalid
     */
    private void validatePlayerCount(int playerCount) throws PlayerNumbersException {
        if (playerCount <= 0 || playerCount > boardService.getMaxPlayers()) {
            throw new PlayerNumbersException("Number of players must be between 1 and " + boardService.getMaxPlayers() + ".");
        }
    }

    /**
     * Initializes the game state with the given list of players.
     *
     * @param playerList the list of players
     * @return the initial game state
     */
    private GameState initGameState(List<Player> playerList) {
        GameState gameState = new GameState();
        gameState.setPlayerList(playerList);
        gameState.setNextPlayer(0);
        gameState.setWinnerDeclared(false);
        return gameState;
    }

    /**
     * Updates the game state to set the next player's turn.
     *
     * @param game the game to update
     */
    public void updateNextPlayer(Game game) {
        GameState gameState = game.getGameState();
        int playerCount = gameState.getPlayerList().size();
        gameState.setNextPlayer((gameState.getNextPlayer() + 1) % playerCount);
    }

    /**
     * Retrieves a game from the repository by its ID.
     *
     * @param gameId the ID of the game to retrieve
     * @return the game
     * @throws GameNotFoundException if the game is not found
     */
    public Game getGameFromRepository(UUID gameId) throws GameNotFoundException {
        return gameRepository.findByIdWithPlayersOptimized(gameId)
                .orElseThrow(() -> new GameNotFoundException("Game with ID: " + gameId + " was not found."));
    }

}