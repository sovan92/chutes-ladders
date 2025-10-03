package com.sovan.chutesladders.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

/**
 * Represents the data transfer object for a player.
 */
@Data
@ToString
@Schema(description = "Player information in the Chutes and Ladders game")
public class PlayerDTO {
    /**
     * The name of the player.
     */
    @Schema(description = "The name of the player", example = "Player 1", required = true)
    private String name;

    /**
     * The current position of the player on the board.
     */
    @Schema(description = "Current position of the player on the game board (0-100)", example = "0", minimum = "0", maximum = "100")
    private int position;

    /**
     * Whether the player has won the game.
     */
    @Schema(description = "Indicates if the player has won the game", example = "false")
    private boolean isWinner;
}
