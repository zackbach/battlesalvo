package cs3500.pa03.controller;

import cs3500.pa03.model.BattleSalvoConfig;
import cs3500.pa03.model.Coord;
import cs3500.pa03.model.GameResult;
import cs3500.pa03.model.Player;
import cs3500.pa03.model.Ship;
import cs3500.pa03.model.ShipType;
import cs3500.pa03.view.GameView;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * Represents a controller to a game of BattleSalvo,
 * where the user is prompted for input when initializing boards
 */
public class ManualController implements GameController {
  private final Player player;
  private final Player opponent;
  private final GameView view;
  
  /**
   * Constructs a new ManualController to play a game between two players,
   * where input and output are handled using the provided View
   *
   * @param player a player of the game
   * @param opponent a player of the game
   * @param view the view used for input/output
   */
  public ManualController(Player player, Player opponent, GameView view) {
    this.player = player;
    this.opponent = opponent;
    this.view = view;
  }

  /**
   * Begins and runs game of BattleSalvo. The user is prompted to enter
   * board dimensions and ship types. Then the gameplay loop starts and 
   * runs until a Player wins
   */
  public void run() {
    this.view.displayMessage("Welcome to BattleSalvo!");
    
    // set up the players
    List<Integer> dimensions = this.getDimensions();
    int height = dimensions.get(0);
    int width = dimensions.get(1);
    Map<ShipType, Integer> specs = this.getSpecifications(Math.min(height, width));
    List<Ship> ships = this.player.setup(height, width, specs);
    List<Ship> oppoShips = this.opponent.setup(height, width, specs);
    
    // main gameplay loop: run until it returns (ends)
    int shipCount = ships.size();
    int oppoShipCount = oppoShips.size();
    // while both players still have ships, continue play
    while (shipCount > 0 && oppoShipCount > 0) {
      List<Coord> playerShots = this.player.takeShots();
      List<Coord> oppoShots = this.opponent.takeShots();
      this.player.successfulHits(this.opponent.reportDamage(playerShots));
      this.opponent.successfulHits(this.player.reportDamage(oppoShots));
      
      // determining when game is ended based solely off of length of takeShots runs into problems:
      // if there are no more coordinates to fire at, the AI will return a zero-length list,
      // which is impossible to differentiate between empty because it has no more ships
      shipCount = ships.stream().filter(Predicate.not(Ship::isSunk)).mapToInt(s -> 1).sum();
      oppoShipCount = oppoShips.stream().filter(Predicate.not(Ship::isSunk)).mapToInt(s -> 1).sum();
    }

    this.handleEndGame(shipCount, oppoShipCount);
  }

  /**
   * Prompts the user to enter dimensions within a specified range, 
   * and returns the dimensions as a list: [height, width]
   *
   * @return a list of the entered height, then width
   */
  private List<Integer> getDimensions() {
    this.view.displayMessage("Please enter a valid height and width:");
    
    while (true) {
      List<Integer> dimensions = this.view.getSomeInts(2, "Please enter exactly two integers.");
      int height = dimensions.get(0);
      int width = dimensions.get(1);

      if (BattleSalvoConfig.MIN_HEIGHT <= height && height <= BattleSalvoConfig.MAX_HEIGHT
          && BattleSalvoConfig.MIN_WIDTH <= width && width <= BattleSalvoConfig.MAX_WIDTH) {
        return dimensions; // breaks out of while loop
      } else {
        this.view.displayMessage("Please enter dimensions within the range [6, 15], inclusive.");
      }
    }
  }

  /**
   * Prompts the user to enter (positive) ship specifications and returns
   * those specifications as a map
   *
   * @param maxShips the maximum number of ships allowed
   * @return a map of the entered number of each ShipType
   */
  private Map<ShipType, Integer> getSpecifications(int maxShips) {
    this.view.displayMessage("Please enter your fleet in the order "
        + "[Carrier, Battleship, Destroyer, Submarine].");
    this.view.displayMessage(String.format("Your fleet size may not exceed %d"
        + " and must contain at least one of each ship.", maxShips));

    while (true) {
      List<Integer> specs = this.view.getSomeInts(4, "Please enter exactly four integers.");

      // checks that the minimum is greater than zero and the sum is less than the max possible
      if (specs.stream().mapToInt(Integer::intValue).min().orElse(0) > 0
          && specs.stream().mapToInt(Integer::intValue).sum() <= maxShips) {
        Map<ShipType, Integer> map = new HashMap<>();
        // order is specified in the prompt
        // it's probably better to hardcode than to use ShipType.TYPE.ordinal()
        map.put(ShipType.CARRIER, specs.get(0));
        map.put(ShipType.BATTLESHIP, specs.get(1));
        map.put(ShipType.DESTROYER, specs.get(2));
        map.put(ShipType.SUBMARINE, specs.get(3));
        return map;
      } else {
        this.view.displayMessage("Please enter a valid fleet size.");
      }
    }
  }

  /**
   * Handles the end of game, given the amount of ships left on boards.
   * A message is displayed using the view depending on who the "player"
   * and who the "opponent" are.
   *
   * @param shipCount the number of ships left on the player's board
   * @param oppoShipCount the number of ships left on the opponent's board
   */
  private void handleEndGame(int shipCount, int oppoShipCount) {
    // handle game end by calling endGame on both
    if (shipCount == 0 && oppoShipCount > 0) {
      this.player.endGame(GameResult.LOSE, "You lost!");
      this.opponent.endGame(GameResult.WIN, "You won!");
      this.view.displayMessage("You lost the game!");
    } else if (shipCount > 0 && oppoShipCount == 0) {
      this.player.endGame(GameResult.WIN, "You won!");
      this.opponent.endGame(GameResult.LOSE, "You lost!");
      this.view.displayMessage("You won the game!");
    } else {
      this.player.endGame(GameResult.DRAW, "You tied!");
      this.opponent.endGame(GameResult.DRAW, "You tied!");
      this.view.displayMessage("You tied the game!");
    }
  }
}
