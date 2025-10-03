package com.sovan.chutesladders.controller;

import com.sovan.chutesladders.exception.GameNotFoundException;
import com.sovan.chutesladders.exception.InconsistentGameException;
import com.sovan.chutesladders.exception.NotYourTurnException;
import com.sovan.chutesladders.exception.PlayerNumbersException;
import com.sovan.chutesladders.model.GameDTO;
import com.sovan.chutesladders.model.PlayerDTO;
import com.sovan.chutesladders.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controller for handling game-related requests.
 */
@Controller
@RequestMapping(path = "/v1/chutesandladders")
@AllArgsConstructor
@Tag(name = "Chutes and Ladders Game", description = "API for managing Chutes and Ladders game operations")
public class GameController {

    private GameService gameService;

    /**
     * Sets up a new game with the given players.
     */
    @Operation(
        summary = "Create a new Chutes and Ladders game",
        description = "Creates a new game instance with the provided list of players. Each player needs a name to participate in the game."
    )
    @RequestBody(
        description = "List of players to participate in the game",
        required = true,
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = PlayerDTO[].class),
            examples = @ExampleObject(
                name = "Three Players Example",
                summary = "Example with three players",
                value = """
                    [
                      {
                        "name": "Player 1"
                      },
                      {
                        "name": "Player 2"
                      },
                      {
                        "name": "Player 3"
                      }
                    ]
                    """
            )
        )
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Game created successfully",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = GameDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid number of players or player data",
            content = @Content
        )
    })
    @PostMapping
    public ResponseEntity<GameDTO> gameSetUp(@org.springframework.web.bind.annotation.RequestBody List<PlayerDTO> playerDTOList) throws PlayerNumbersException {
        GameDTO game = gameService.createNewGame(playerDTOList);
        return ResponseEntity.of(Optional.of(game));
    }

    /**
     * Updates the game state by playing a turn for the given player.
     */
    @Operation(
        summary = "Play a turn in the game",
        description = "Executes a turn for the specified player in the game. The player name must match the current player's turn."
    )
    @RequestBody(
        description = "Current game state with all players and game information",
        required = true,
        content = @Content(
            mediaType = MediaType.APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = GameDTO.class),
            examples = @ExampleObject(
                name = "Game State Example",
                summary = "Example game state for playing a turn",
                value = """
                    {
                      "gameId": "32dbcbc1-04e7-4ac9-8c1b-bc46414bdc03",
                      "gameState": {
                        "playerList": [
                          {
                            "name": "Player 1",
                            "position": 0,
                            "winner": false
                          },
                          {
                            "name": "Player 2",
                            "position": 0,
                            "winner": false
                          },
                          {
                            "name": "Player 3",
                            "position": 0,
                            "winner": false
                          }
                        ],
                        "nextPlayer": 0,
                        "winnerDeclared": false
                      }
                    }
                    """
            )
        )
    )
    @Parameter(
        name = "X-Player-Name",
        description = "Name of the player making the move",
        required = true,
        example = "Player 1"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Turn played successfully",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = GameDTO.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Invalid game data or player information",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "404",
            description = "Game not found",
            content = @Content
        ),
        @ApiResponse(
            responseCode = "409",
            description = "Not the player's turn or inconsistent game state",
            content = @Content
        )
    })
    @PutMapping
    public ResponseEntity<GameDTO> updateGame(
        @org.springframework.web.bind.annotation.RequestBody GameDTO game,
        @RequestHeader("X-Player-Name") String playerName
    ) throws PlayerNumbersException, NotYourTurnException, GameNotFoundException, InconsistentGameException {
        GameDTO updatedGame = gameService.playGame(game, playerName);
        return ResponseEntity.of(Optional.of(updatedGame));
    }


}