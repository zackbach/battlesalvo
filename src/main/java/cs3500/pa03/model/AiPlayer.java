package cs3500.pa03.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Represents an AI BattleSalvo player
 */
public class AiPlayer extends AbstractPlayer {

  /**
   * Constructs a new AiPlayer with the given random number generator
   *
   * @param rand the random number generator to use
   */
  public AiPlayer(Random rand) {
    super(rand);
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
    // as per Piazza @777_f1, hardcoded player name
    return "AI Player";
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
    
    // while the AI logic can (and will) be improved later,
    // it currently randomly fires shots among locations that have not been hit
    List<Coord> randomOptions = new ArrayList<>(this.getUnfiredShots());
    Collections.shuffle(randomOptions, this.rand);
    
    // if there are more ships left than unfired locations, 
    // this will fire shots equal to the number of unfired locations 
    List<Coord> shots = randomOptions.stream().limit(this.board.getUnsunkShipTotal()).toList();
    this.prevSalvo.addAll(shots);
    return shots;
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
