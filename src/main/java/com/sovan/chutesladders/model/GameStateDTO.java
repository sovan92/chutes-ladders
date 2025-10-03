package com.sovan.chutesladders.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import java.util.List;

/**
 * Represents the data transfer object for the game state.
 */
@Data
@ToString
@Schema(description = "Current state of the Chutes and Ladders game")
public class GameStateDTO {
    /**
     * The list of players in the game.
     */
    @Schema(description = "List of all players participating in the game", required = true)
    private List<PlayerDTO> playerList;

    /**
     * The index of the next player to play.
     */
    @Schema(description = "Index of the player whose turn is next (0-based)", example = "0", minimum = "0")
    private int nextPlayer = 0;

    /**
     * Whether a winner has been declared.
     */
    @Schema(description = "Indicates if a winner has been declared in the game", example = "false")
    boolean isWinnerDeclared = false;
}
