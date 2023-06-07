package cs3500.pa04;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a message sent by the player to join a game on the server
 *
 * @param name the name of some player
 * @param gameType the type of game to be player
 */
public record JoinJson(
    @JsonProperty("name") String name,
    @JsonProperty("game-type") GameType gameType) {
}
