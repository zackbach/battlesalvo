package cs3500.pa04;

import com.fasterxml.jackson.annotation.JsonProperty;
import cs3500.pa03.model.Ship;
import java.util.List;

/**
 * Represents a list of ships as a fleet
 *
 * @param fleet the ships in this fleet
 */
public record FleetJson(
    @JsonProperty("fleet") List<Ship> fleet) {
}
