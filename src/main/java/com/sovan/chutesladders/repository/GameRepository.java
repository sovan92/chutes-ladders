package com.sovan.chutesladders.repository;

import com.sovan.chutesladders.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository for accessing game data.
 */
@Repository
public interface GameRepository extends JpaRepository<Game, UUID> {

    /**
     * Finds a game by ID with all related entities fetched in a single query to prevent N+1 issues.
     * This uses JOIN FETCH to eagerly load GameState and Player entities.
     *
     * @param gameId the ID of the game to find
     * @return Optional containing the game with all related data loaded, or empty if not found
     */
    @Query("SELECT g FROM Game g " +
           "LEFT JOIN FETCH g.gameState gs " +
           "LEFT JOIN FETCH gs.playerList " +
           "WHERE g.gameId = :gameId")
    Optional<Game> findByIdWithPlayersOptimized(@Param("gameId") UUID gameId);

}
