package com.sovan.chutesladders.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.UUID;

/**
 * Represents a game of Chutes and Ladders.
 */
@Data
@Entity
@ToString
public class Game {

    /**
     * The unique identifier for the game.
     */
    @Id
    private UUID gameId;

    /**
     * The current state of the game.
     */
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "game_state_id", referencedColumnName = "id")
    private GameState gameState;

    /**
     * The version number for optimistic locking.
     */
    @Version
    private Long version;
}
