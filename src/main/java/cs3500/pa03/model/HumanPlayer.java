package cs3500.pa03.model;

import cs3500.pa03.view.GameView;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * Represents a HumanPlayer implementation of the Player interface.
 * The player is prompted using a Scanner and a GameView
 */
public class HumanPlayer extends AbstractPlayer {
  private final GameView view;
  
  /**
   * Constructs a new HumanPlayer with the given view for handling inputs/outputs
   * and random numbers generated using the given Random generator
   *
   * @param view the view for input/output
   * @param rand the random generator
   */
  public HumanPlayer(GameView view, Random rand) {
    super(rand);
    this.view = Objects.requireNonNull(view);
  }

  /**
   * Constructs a new HumanPlayer with the given view for handling inputs/outputs
   *
   * @param view the view for input/output
   */
  public HumanPlayer(GameView view) {
    // this will be called when random should be unseeded
    this(view, new Random());
  }

  /**
   * Get the human player's (hardcoded) name.
   *
   * @return the player's name
   */
  @Override
  public String name() {
    // as per Piazza @777_f1, hardcoded player name
    return "Human Player";
  }

  /**
   * Returns this player's shots on the opponent's board. The number of shots returned should
   * equal the number of ships on this player's board that have not sunk. The user is prompted
   * to enter shots using the provided GameView
   *
   * @return the locations of shots on the opponent's board
   * @throws IllegalStateException if the board has not been set up
   */
  @Override
  public List<Coord> takeShots() {
    if (!this.isSetup) {
      throw new IllegalStateException("board has not been set up");
    }
    
    this.view.displayMessage("Opponent's Board:");
    this.view.displayMessage(this.opponentBoard.toString());
    this.view.displayMessage(""); // newline
    
    this.view.displayMessage("Your Board:");
    this.view.displayMessage(this.board.toString());
    this.view.displayMessage("");

    this.view.displayMessage(String.format("Please enter %d shots:", 
        this.board.getUnsunkShipTotal()));
    
    List<Coord> coords = new ArrayList<>();
    
    for (int i = 0; i < this.board.getUnsunkShipTotal(); i++) {
      // continue prompting until a valid coordinate is entered
      while (true) {
        List<Integer> input = this.view.getSomeInts(2, "Please enter exactly 2 integers");
        int x = input.get(0);
        int y = input.get(1);
        
        // if it is a valid input...
        if (0 <= x && x < this.board.getWidth() && 0 <= y && y < this.board.getHeight()) {
          // DESIGN DECISION: it is okay for the player to fire at a previously-hit location
          coords.add(new Coord(x, y));
          break; // no longer continue asking
        }
        this.view.displayMessage("Please enter coordinates that are within the board's dimensions");
      }
    }

    this.prevSalvo.addAll(coords);
    return coords;
  }
}
