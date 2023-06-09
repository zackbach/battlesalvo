package cs3500.pa04;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cs3500.pa03.model.AiPlayer;
import cs3500.pa03.model.Coord;
import cs3500.pa03.model.GameResult;
import cs3500.pa03.model.Player;
import cs3500.pa03.model.ShipType;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Represents a method to test the ProxyController
 */
class ProxyControllerTest {
  // we store the setup and end game json that often must be sent
  private SetupJson setupJson;
  private EndJson endJson;
  private Player ai;
  private ByteArrayOutputStream testLog;
  private ObjectMapper mapper;

  /**
   * Sets up the test harness by initializing the setupConfig and Player,
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
    
    this.setupJson = new SetupJson(6, 6, spec);
    this.endJson = new EndJson(GameResult.WIN, "You won!");
    this.ai = new AiPlayer(new Random(1));
    this.mapper = new ObjectMapper();
    
    // clear out the log
    this.testLog = new ByteArrayOutputStream(2048);
    assertEquals("", testLog.toString(StandardCharsets.UTF_8));
  }

  /**
   * Tests that requests to join the server are properly handled
   */
  @Test
  public void testJoin() {
    JsonNode joinMessage = this.createVoidMessage("join");
    // we have to close the server too, otherwise ProxyController waits for more input
    JsonNode endMessage = this.createSampleMessage("end-game", this.endJson);
    Socket mocket = new Mocket(this.testLog, 
        List.of(joinMessage.toString(), endMessage.toString()));
    
    try {
      // this should read the join message and output the response
      new ProxyController(mocket, this.ai).run();
      String[] jsonLog = this.testLog.toString().split(System.lineSeparator());
      // the jsonLog consists of each of the messages from the server
      // which we know are separated by newlines.
      // we test the EndJson in testEndGame below
      // for now, we just check that it exists.
      assertEquals(2, jsonLog.length);
      
      MessageJson join = this.responseToClass(jsonLog[0]);
      assertEquals("join", join.methodName());
      JoinJson joinResponse = this.mapper.convertValue(join.arguments(), JoinJson.class);
      // hard-coded to be SINGLE, as per Piazza @1073
      assertEquals(new JoinJson("zackbach", GameType.SINGLE), joinResponse);
    } catch (IOException e) {
      // this occurs if there is a JSON parsing exception, in which case we should fail
      fail();
    }
  }

  /**
   * Tests that requests to set up the player are properly handled
   */
  @Test
  public void testSetup() {
    JsonNode setupMessage = this.createSampleMessage("setup", this.setupJson);
    JsonNode endMessage = this.createSampleMessage("end-game", this.endJson);
    Socket mocket = new Mocket(this.testLog,
        List.of(setupMessage.toString(), endMessage.toString()));

    try {
      // this should read the join message and output the response
      new ProxyController(mocket, this.ai).run();
      String[] jsonLog = this.testLog.toString().split(System.lineSeparator());
      assertEquals(2, jsonLog.length);
      
      MessageJson setup = this.responseToClass(jsonLog[0]);
      assertEquals("setup", setup.methodName());
      
      FleetJson setupResponse = this.mapper.convertValue(setup.arguments(), FleetJson.class);
      // tests that all 4 ships were placed on the board.
      // we already test in AiPlayer that ship placement does not overlap, etc
      assertEquals(4, setupResponse.fleet().size());
    } catch (IOException e) {
      fail();
    }
  }

  /**
   * Tests that requests to take shots and the player are properly handled,
   * and that successful coordinates are handled appropriately
   */
  @Test
  public void testTakeShots() {
    JsonNode setupMessage = this.createSampleMessage("setup", this.setupJson);
    JsonNode shotsMessage = this.createVoidMessage("take-shots");
    
    List<Coord> hits = List.of(new Coord(0, 0), new Coord(1, 0), new Coord(2, 0), new Coord(3, 0));
    JsonNode successful = this.createSampleMessage("successful-hits", new CoordinatesJson(hits));

    JsonNode endMessage = this.createSampleMessage("end-game", this.endJson);
    Socket mocket = new Mocket(this.testLog, List.of(setupMessage.toString(),
        shotsMessage.toString(), successful.toString(), endMessage.toString()));

    try {
      // this should read the join message and output the response
      new ProxyController(mocket, this.ai).run();
      String[] jsonLog = this.testLog.toString().split(System.lineSeparator());
      assertEquals(4, jsonLog.length);

      // test the take-shots response
      MessageJson shots = this.responseToClass(jsonLog[1]);
      assertEquals("take-shots", shots.methodName());
      CoordinatesJson shotsResponse = this.mapper.convertValue(shots.arguments(), 
          CoordinatesJson.class);
      // we seed random to know what these shots always are
      assertEquals(List.of(new Coord(4, 1), new Coord(2, 2), new Coord(3, 4), new Coord(3, 3)), 
          shotsResponse.coordinates());

      // test the successful-hits response is null json
      MessageJson successfulHits = this.responseToClass(jsonLog[2]);
      assertEquals(this.createVoidMessage("successful-hits"),
          this.mapper.convertValue(successfulHits, JsonNode.class));

    } catch (IOException e) {
      fail();
    }
  }

  /**
   * Tests that requests to report damage are properly handled
   */
  @Test
  public void testReportDamage() {
    JsonNode setupMessage = this.createSampleMessage("setup", this.setupJson);
    
    List<Coord> fired = List.of(new Coord(0, 0), new Coord(1, 0), new Coord(2, 0), new Coord(3, 0));
    JsonNode reportMessage = this.createSampleMessage("report-damage", new CoordinatesJson(fired));
    
    JsonNode endMessage = this.createSampleMessage("end-game", this.endJson);
    Socket mocket = new Mocket(this.testLog, 
        List.of(setupMessage.toString(), reportMessage.toString(), endMessage.toString()));

    try {
      // this should read the join message and output the response
      new ProxyController(mocket, this.ai).run();

      String[] jsonLog = this.testLog.toString().split(System.lineSeparator());
      assertEquals(3, jsonLog.length);

      // we test setup and end game elsewhere, so we only check reported damage
      MessageJson reported = this.responseToClass(jsonLog[1]);
      assertEquals("report-damage", reported.methodName());

      CoordinatesJson reportedResponse = this.mapper.convertValue(reported.arguments(), 
          CoordinatesJson.class);
      // we know exactly which shots will hit, since we initialize ships with a seeded random
      assertEquals(List.of(new Coord(0, 0), new Coord(3, 0)), reportedResponse.coordinates());
    } catch (IOException e) {
      fail();
    }

  }

  /**
   * Tests that requests to end the game are properly handled.
   * Note that tests above assume that this works as intended,
   * since the Mocket must be closed.
   */
  @Test
  public void testEndGame() {
    JsonNode endMessage = this.createSampleMessage("end-game", this.endJson);
    Socket mocket = new Mocket(this.testLog, List.of(endMessage.toString()));

    try {
      // this should read the join message and output the response
      new ProxyController(mocket, this.ai).run();

      MessageJson end = this.responseToClass(this.testLog.toString());
      // tests that the end game will return a void response with "end-game" method-name
      assertEquals(this.createVoidMessage("end-game"), 
          this.mapper.convertValue(end, JsonNode.class));
    } catch (IOException e) {
      fail();
    }
  }

  /**
   * Tests that exceptions are handled
   */
  @Test
  public void testExceptions() {
    // tests that sending a non-MessageJson throws an exception
    Socket badJson = new Mocket(this.testLog, List.of(this.createVoidMessage("bad").toString()));
    // tests that throwing a MessageJson with invalid method name throws an exception
    assertThrows(IllegalArgumentException.class, () -> new ProxyController(badJson, this.ai).run());
  }


  /**
   * Converts the given string of Json into MessageJson
   *
   * @param jsonString the string to parse into MessageJson
   * @return the jsonString as the new class
   * @throws IOException if the JSON is unable to be parsed properly
   */
  private MessageJson responseToClass(String jsonString) throws IOException {
    // note: this was adapted from the example Mocket testing example
    try (JsonParser jsonParser = this.mapper.createParser(jsonString)) {
      return jsonParser.readValueAs(MessageJson.class);
    }
  }
  
  /**
   * Create a MessageJson for some name and arguments.
   *
   * @param methodName the method name of the message
   * @param messageObject object to embed in the MessageJson
   * @return the converted MessageJson
   */
  private JsonNode createSampleMessage(String methodName, Record messageObject) {
    // convert given record to MessageJson record
    MessageJson message = new MessageJson(methodName,
        this.mapper.convertValue(messageObject, JsonNode.class));
    
    // convert MessageJson record to JsonNode
    return this.mapper.convertValue(message, JsonNode.class);
  }

  /**
   * Create a MessageJson for a method name with null arguments.
   *
   * @param methodName the method name of the message
   * @return the converted MessageJson
   */
  private JsonNode createVoidMessage(String methodName) {
    MessageJson message = new MessageJson(methodName, this.mapper.createObjectNode());
    return this.mapper.convertValue(message, JsonNode.class);
  }
}