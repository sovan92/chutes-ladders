package com.sovan.chutesladders.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.ToString;

/**
 * Represents a player in the game.
 */
@Data
@Entity
@ToString
public class Player {

    /**
     * The unique identifier for the player.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * The name of the player.
     */
    private String name;
    /**
     * The current position of the player on the board.
     */
    private int position;
    /**
     * Whether the player has won the game.
     */
    private boolean isWinner = false;
}
