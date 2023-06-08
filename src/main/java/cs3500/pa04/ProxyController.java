package cs3500.pa04;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cs3500.pa03.controller.GameController;
import cs3500.pa03.model.Coord;
import cs3500.pa03.model.Player;
import cs3500.pa03.model.Ship;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.List;

/**
 * Represents a ProxyController in a game of BattleSalvo, allowing
 * Players to communicate using a server
 */
public class ProxyController implements GameController {
  private final Socket server;
  private final InputStream in;
  private final PrintStream out;
  private final Player player;
  private final GameType type;
  private final ObjectMapper mapper;

  /**
   * Constructs a new ProxyController using the given socket, player, and GameType
   *
   * @param server the server to connect to
   * @param player the player that is playing on the server
   * @throws IOException if there is an error with the Socket
   */
  public ProxyController(Socket server, Player player, GameType type) throws IOException {
    this.server = server;
    this.in = server.getInputStream();
    this.out = new PrintStream(server.getOutputStream());
    this.player = player;
    this.type = type;
    this.mapper = new ObjectMapper();
  }

  /**
   * Constructs a new ProxyController using the given socket and player,
   * with a default GameType of single-player
   *
   * @param server the server to connect to
   * @param player the player that is playing on the server
   * @throws IOException if there is an error with the Socket
   */
  public ProxyController(Socket server, Player player) throws IOException {
    this(server, player, GameType.SINGLE);
  }

  /**
   * Begins and runs game of BattleSalvo, starting the gameplay loop. Listens to the
   * server to receive JSON messages, then parses, processes, and sends responses
   * using the behavior of the Player.
   */
  @Override
  public void run() {
    try {
      JsonParser parser = this.mapper.getFactory().createParser(this.in);
      
      while (!this.server.isClosed()) {
        // note that we do not have to check if it is well-formed,
        // since we can assume that the server always sends appropriate MessageJson
        MessageJson message = parser.readValueAs(MessageJson.class);
        this.delegateMessage(message);
      }
    } catch (IOException e) {
      // Disconnected from server or parsing exception
      // TODO: Do we have to do anything here?
    }
  }

  /**
   * Determines the type of request the server has sent and delegates
   * to the corresponding helper method with the message arguments
   *
   * @param message the MessageJson sent by the server
   */
  private void delegateMessage(MessageJson message) throws IOException {
    String name = message.methodName();
    JsonNode arguments = message.arguments();

    switch (name) {
      case "join" -> this.handleJoin();
      case "setup" -> this.handleSetup(arguments);
      case "take-shots" -> this.handleTakeShots();
      case "report-damage" -> this.handleReportDamage(arguments);
      case "successful-hits" -> this.handleSuccessfulHits(arguments);
      case "end-game" -> this.handleEndGame(arguments);
      default -> throw new IllegalArgumentException("JSON is not well-formed");
    }
  }
  
  /**
   * Handles a response to join a game
   */
  private void handleJoin() {
    JoinJson response = new JoinJson(this.player.name(), this.type);
    JsonNode jsonResponse = this.mapper.convertValue(response, JsonNode.class);
    MessageJson message = new MessageJson("join", jsonResponse);
    JsonNode jsonMessage = this.mapper.convertValue(message, JsonNode.class);
    this.out.println(jsonMessage);
  }

  /**
   * Handles a response to set up a game with the given arguments
   *
   * @param arguments the arguments to be parsed into SetupJson
   */
  private void handleSetup(JsonNode arguments) {
    SetupJson parsed = this.mapper.convertValue(arguments, SetupJson.class);
    List<Ship> setupShips = this.player.setup(parsed.height(), parsed.width(), parsed.fleetSpec());
    
    FleetJson response = new FleetJson(setupShips);
    JsonNode jsonResponse = this.mapper.convertValue(response, JsonNode.class);
    MessageJson message = new MessageJson("setup", jsonResponse);
    JsonNode jsonMessage = this.mapper.convertValue(message, JsonNode.class);
    this.out.println(jsonMessage);
  }

  /**
   * Handles a response to take shots
   */
  private void handleTakeShots() {
    CoordinatesJson response = new CoordinatesJson(this.player.takeShots());
    JsonNode jsonResponse = this.mapper.convertValue(response, JsonNode.class);
    MessageJson message = new MessageJson("take-shots", jsonResponse);
    JsonNode jsonMessage = this.mapper.convertValue(message, JsonNode.class);
    this.out.println(jsonMessage);
  }

  /**
   * Handles a response to report damage
   *
   * @param arguments the arguments to be parsed into CoordinatesJson
   */
  private void handleReportDamage(JsonNode arguments) {
    CoordinatesJson parsed = this.mapper.convertValue(arguments, CoordinatesJson.class);
    List<Coord> coords = this.player.reportDamage(parsed.coordinates());

    CoordinatesJson response = new CoordinatesJson(coords);
    JsonNode jsonResponse = this.mapper.convertValue(response, JsonNode.class);
    MessageJson message = new MessageJson("report-damage", jsonResponse);
    JsonNode jsonMessage = this.mapper.convertValue(message, JsonNode.class);
    this.out.println(jsonMessage);
  }

  /**
   * Handles a response to report successful hits
   *
   * @param arguments the arguments to be parsed into CoordinatesJson
   */
  private void handleSuccessfulHits(JsonNode arguments) {
    CoordinatesJson parsed = this.mapper.convertValue(arguments, CoordinatesJson.class);
    this.player.successfulHits(parsed.coordinates());

    MessageJson message = new MessageJson("successful-hits", this.mapper.createObjectNode());
    JsonNode jsonMessage = this.mapper.convertValue(message, JsonNode.class);
    this.out.println(jsonMessage);
  }

  /**
   * Handles a response to end a game with the given arguments
   *
   * @param arguments the arguments to be parsed into EndJson
   */
  private void handleEndGame(JsonNode arguments) throws IOException {
    EndJson parsed = this.mapper.convertValue(arguments, EndJson.class);
    this.player.endGame(parsed.result(), parsed.reason());
    
    MessageJson message = new MessageJson("end-game", this.mapper.createObjectNode());
    JsonNode jsonMessage = this.mapper.convertValue(message, JsonNode.class);
    this.out.println(jsonMessage);
    
    System.out.println(parsed.result());
    System.out.println(parsed.reason());
    this.server.close();
  }
}
