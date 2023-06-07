package cs3500.pa04;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * Represents messages that are sent to and by a server
 *
 * @param methodName the name of the relevant method
 * @param arguments the arguments/results of the relevant method
 */
public record MessageJson(
    @JsonProperty("method-name") String methodName,
    @JsonProperty("arguments") JsonNode arguments) {
}
