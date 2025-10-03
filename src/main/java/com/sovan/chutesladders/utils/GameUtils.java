package com.sovan.chutesladders.utils;

import com.sovan.chutesladders.model.*;
import lombok.extern.slf4j.Slf4j;
import org.thymeleaf.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * Utility class for game-related operations.
 */
@Slf4j
public class GameUtils {

    /**
     * Checks if a game entity is equal to a game DTO.
     *
     * @param objDatabase the game entity from the database
     * @param obj         the game DTO from the request
     * @return true if the objects are equal, false otherwise
     */
    public static boolean isGameEqual(Game objDatabase, GameDTO obj) {
        return isUUIDEqual(objDatabase, obj) && isGameStateEqual(objDatabase.getGameState(), obj.getGameState()) ;
    }

    /**
     * Checks if the UUID of a game entity is equal to the UUID of a game DTO.
     *
     * @param objDatabase the game entity from the database
     * @param obj         the game DTO from the request
     * @return true if the UUIDs are equal, false otherwise
     */
    public static boolean isUUIDEqual(Game objDatabase, GameDTO obj) {
        return objDatabase.getGameId().equals(obj.getGameId());
    }

    /**
     * Checks if the game state of a game entity is equal to the game state of a game DTO.
     *
     * @param gameStateDb  the game state from the database
     * @param gameStateReq the game state from the request
     * @return true if the game states are equal, false otherwise
     */
    public static boolean isGameStateEqual(GameState gameStateDb, GameStateDTO gameStateReq) {

        return (gameStateDb.getNextPlayer() == gameStateReq.getNextPlayer()) &&
                (gameStateDb.isWinnerDeclared() == gameStateReq.isWinnerDeclared()) && isPlayerListEqual(gameStateDb, gameStateReq);
    }

    /**
     * Checks if the player list of a game entity is equal to the player list of a game DTO.
     *
     * @param gameStatedb  the game state from the database
     * @param gameStateReq the game state from the request
     * @return true if the player lists are equal, false otherwise
     */
    public static boolean isPlayerListEqual(GameState gameStatedb, GameStateDTO gameStateReq) {
        List<Player> playerlistDb = gameStatedb.getPlayerList();
        List<PlayerDTO> playerlistReq = gameStateReq.getPlayerList();
        for (int i = 0; i < playerlistDb.size(); i++) {
            Player p1 = playerlistDb.get(i);
            PlayerDTO p2 = playerlistReq.get(i);
            if (p1 == null || p2 == null || !StringUtils.equals(p1.getName(), p2.getName()) || p1.getPosition() != p2.getPosition()) {
                return false;
            }
        }
        return true;
    }


}
