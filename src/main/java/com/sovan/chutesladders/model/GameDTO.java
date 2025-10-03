package com.sovan.chutesladders.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import java.util.UUID;

/**
 * Represents the data transfer object for a game.
 */
@Data
@ToString
@Schema(description = "Complete game information including ID and current state")
public class GameDTO {
    /**
     * The unique identifier for the game.
     */
    @Schema(description = "Unique identifier for the game session", example = "32dbcbc1-04e7-4ac9-8c1b-bc46414bdc03")
    private UUID gameId;

    /**
     * The current state of the game.
     */
    @Schema(description = "Current state of the game including players and turn information", required = true)
    private GameStateDTO gameState;
}
