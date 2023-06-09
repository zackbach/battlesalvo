package cs3500.pa04;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cs3500.pa03.model.ShipType;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProxyControllerTest {
  private SetupJson setupConfig;
  private ByteArrayOutputStream testLog;

  /**
   * Sets up the test harness by initializing the setupConfig
   * and clearing the testLog
   */
  @BeforeEach
  public void setup() {
    // since the board must be set up before calling other methods on Player,
    // we store the setupJson response as a field for easy access
    Map<ShipType, Integer> spec = new HashMap<>();
    for (ShipType t : ShipType.values()) {
      spec.put(t, 1);
    }
    
    this.setupConfig = new SetupJson(6, 6, spec);

    // clear out the log
    this.testLog = new ByteArrayOutputStream(2048);
    assertEquals("", testLog.toString(StandardCharsets.UTF_8));
  }

  /**
   * Tests that requests to join the server are properly handled
   */
  @Test
  public void testJoin() {
    
  }

  /**
   * Tests that requests to set up the player are properly handled
   */
  @Test
  public void testSetup() {

  }

  /**
   * Tests that requests to take shots and the player are properly handled,
   * and that successful coordinates are handled appropriately
   */
  @Test
  public void testTakeShots() {

  }

  /**
   * Tests that requests to report damage are properly handled
   */
  @Test
  public void testReportDamage() {

  }

  /**
   * Tests that requests to end the game are properly handled
   */
  @Test
  public void testEndGame() {

  }
  
  /**
   * Create a MessageJson for some name and arguments.
   *
   * @param methodName the method name of the message
   * @param messageObject object to embed in the MessageJson
   * @return the converted MessageJson
   */
  private JsonNode createSampleMessage(String methodName, Record messageObject) {
    ObjectMapper mapper = new ObjectMapper();
    
    // convert given record to MessageJson record
    MessageJson message = new MessageJson(methodName, 
        mapper.convertValue(messageObject, JsonNode.class));
    
    // convert MessageJson record to JsonNode
    return mapper.convertValue(message, JsonNode.class);
  }

  /**
   * Create a MessageJson for a method name with null arguments.
   *
   * @param methodName the method name of the message
   * @return the converted MessageJson
   */
  private JsonNode createVoidMessage(String methodName) {
    ObjectMapper mapper = new ObjectMapper();
    
    MessageJson message = new MessageJson(methodName, mapper.createObjectNode());
    return mapper.convertValue(message, JsonNode.class);
  }
}