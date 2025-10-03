package com.sovan.chutesladders.utils;

import com.sovan.chutesladders.model.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper for converting between game entities and DTOs.
 */
public class GameMapper {


    /**
     * Converts a list of player DTOs to a list of player entities.
     *
     * @param playerDTOList the list of player DTOs
     * @return the list of player entities
     */
    public static List<Player> getPlayers(List<PlayerDTO> playerDTOList){

        return playerDTOList.stream().map((PlayerDTO playerDTO) -> {
            Player player = new Player();
            player.setPosition(playerDTO.getPosition());
            player.setName(playerDTO.getName());
            player.setWinner(playerDTO.isWinner());
            return player;
        }).collect(Collectors.toList());
    }

    /**
     * Converts a player entity to a player DTO.
     *
     * @param player the player entity
     * @return the player DTO
     */
    private static PlayerDTO getPlayerDto(Player player) {
        PlayerDTO playerDTO = new PlayerDTO();
        playerDTO.setName(player.getName());
        playerDTO.setPosition(player.getPosition());
        playerDTO.setWinner(player.isWinner());
        return playerDTO;
    }

    /**
     * Converts a game entity to a game DTO.
     *
     * @param game the game entity
     * @return the game DTO
     */
    public static GameDTO getGameDto(Game game){
        GameDTO gameDTO = new GameDTO();
        gameDTO.setGameId(game.getGameId());

        GameStateDTO gameStateDTO = new GameStateDTO();
        GameState gameState = game.getGameState();

        gameStateDTO.setNextPlayer(gameState.getNextPlayer());
        gameStateDTO.setWinnerDeclared(gameState.isWinnerDeclared());
        gameStateDTO.setPlayerList(
                gameState.getPlayerList().stream()
                        .map(GameMapper::getPlayerDto)
                        .collect(Collectors.toList())
        );

        gameDTO.setGameState(gameStateDTO);
        return gameDTO;
    }



}
