package com.sovan.chutesladders.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the state of a game.
 */
@Data
@Entity
@ToString
public class GameState {

    /**
     * The unique identifier for the game state.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * The list of players in the game.
     * JoinColumn creates a foreign key relationship instead of a join table.
     */
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "game_state_id")
    private List<Player> playerList = new ArrayList<>();
    /**
     * The index of the next player to play.
     */
    private int nextPlayer = 0;
    /**
     * Whether a winner has been declared.
     */
    boolean isWinnerDeclared = false;

}
