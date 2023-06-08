package cs3500.pa03.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.Random;

/**
 * Represents an AI BattleSalvo player
 */
public class AiPlayer extends AbstractPlayer {
  private final Queue<Coord> nextShots;
  
  /**
   * Constructs a new AiPlayer with the given random number generator
   *
   * @param rand the random number generator to use
   */
  public AiPlayer(Random rand) {
    super(rand);
    this.nextShots = new ArrayDeque<>();
  }

  /**
   * Constructs a new AiPlayer with an unseeded random number generator
   */
  public AiPlayer() {
    this(new Random());
  }

  /**
   * Get the AI player's (hard-coded) name.
   *
   * @return the player's name
   */
  @Override
  public String name() {
    // as per Piazza @777_f1, hardcoded player name = github username
    return "zackbach";
  }

  /**
   * Returns this player's shots on the opponent's board. The number of shots returned should
   * equal the number of ships on this player's board that have not sunk. Shots are randomly
   * selected among locations that have not yet been fired at.
   *
   * @return the locations of shots on the opponent's board
   * @throws IllegalStateException if the board has not been set up
   */
  @Override
  public List<Coord> takeShots() {
    if (!this.isSetup) {
      throw new IllegalStateException("board has not been set up");
    }
    
    int volleySize = this.board.getUnsunkShipTotal();
    List<Coord> shots = new ArrayList<>();
    
    // start by taking shots from the queue
    while (volleySize > 0 && !this.nextShots.isEmpty()) {
      shots.add(this.nextShots.remove());
      volleySize--;
    }
    
    // add remaining shots (if possible)
    List<Coord> randomOptions = new ArrayList<>(this.getUnfiredShots());
    Collections.shuffle(randomOptions, this.rand);
    
    // if there are more ships left than unfired locations, 
    // this will fire shots equal to the number of unfired locations 
    List<Coord> randomShots = randomOptions.stream()
                                           .filter(c -> !shots.contains(c))
                                           .limit(volleySize)
                                           .toList();
    
    shots.addAll(randomShots);
    this.prevSalvo.addAll(shots);
    return shots;
  }

  /**
   * Reports to this player what shots in their previous volley returned from takeShots()
   * successfully hit an opponent's ship.
   *
   * @param shotsThatHitOpponentShips the list of shots that successfully hit the opponent's ships
   * @throws IllegalStateException if the board has not been set up
   */
  @Override
  public void successfulHits(List<Coord> shotsThatHitOpponentShips) {
    super.successfulHits(shotsThatHitOpponentShips);
    // adds neighbors of successful shots to queue after processing hits
    this.addNeighbors(shotsThatHitOpponentShips);
  }
    
  /**
   * Adds the neighbors of the given coords to the nextShots queue
   *
   * @param coords the shots whose neighbors are added
   */
  private void addNeighbors(List<Coord> coords) {
    for (Coord coord : coords) {
      int x = coord.x();
      int y = coord.y();
      List<Coord> neighbors = new ArrayList<>();
      
      neighbors.add(new Coord(x + 1, y));
      neighbors.add(new Coord(x, y + 1));
      // to prevent negative coords from being constructed and throwing an error
      if (x != 0) {
        neighbors.add(new Coord(x - 1, y));
      }
      if (y != 0) {
        neighbors.add(new Coord(x, y - 1));
      }
      
      // take only the neighboring shots that are valid coordinates that have not been tried before
      List<Coord> filtered = neighbors.stream()
                                      .filter(c -> !this.nextShots.contains(c))
                                      .filter(c -> !this.opponentBoard.isInvalidCoord(c))
                                      .filter(c -> 
                                          this.opponentBoard.getCellState(c) == CellState.WATER)
                                      .toList();
      this.nextShots.addAll(filtered);
    }
  }

  /**
   * Returns a list of all coordinates on the opponentBoard that have not been fired at
   *
   * @return a list of all coordinates that are not HIT or MISS
   */
  private List<Coord> getUnfiredShots() {
    List<Coord> coords = new ArrayList<>();
    
    for (int x = 0; x < this.opponentBoard.getWidth(); x++) {
      for (int y = 0; y < this.opponentBoard.getHeight(); y++) {
        Coord c = new Coord(x, y);
        // if the location has not been hit and not been missed, it is a valid option
        if (this.opponentBoard.getCellState(c) != CellState.HIT 
            && this.opponentBoard.getCellState(c) != CellState.MISS) {
          coords.add(c);
        }
      }
    }
    
    return coords;
  }
}
