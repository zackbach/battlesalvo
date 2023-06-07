package cs3500.pa04;

import com.fasterxml.jackson.annotation.JsonProperty;
import cs3500.pa03.model.ShipType;
import java.util.Map;


/**
 * Represents a message containing information about a board's setup
 *
 * @param height the height of the board to set up
 * @param width the width of the board to set up
 * @param fleetSpec the ships that are placed on the board
 */
public record SetupJson(
    @JsonProperty("height") int height,
    @JsonProperty("width") int width,
    @JsonProperty("fleet-spec") Map<ShipType, Integer> fleetSpec) {
}
