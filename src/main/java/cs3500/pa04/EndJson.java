package cs3500.pa04;

import com.fasterxml.jackson.annotation.JsonProperty;
import cs3500.pa03.model.GameResult;

/**
 * Represents message indicating the result of a game of BattleSalvo
 *
 * @param result the result of the completed game
 * @param reason the reason for the game ending
 */
public record EndJson(
    @JsonProperty("result") GameResult result,
    @JsonProperty("reason") String reason) {
}
