package cs3500.pa04;

import com.fasterxml.jackson.annotation.JsonProperty;
import cs3500.pa03.model.Coord;
import java.util.List;

/**
 * Represents a volley as a list of coordinates
 *
 * @param coordinates the ships in this fleet
 */
public record CoordinatesJson(
    @JsonProperty("coordinates") List<Coord> coordinates) {
}
