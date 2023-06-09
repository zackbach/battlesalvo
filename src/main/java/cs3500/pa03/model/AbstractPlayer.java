package cs3500.pa03.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

/**
 * Represents an AbstractPlayer implementation of the Player interface.
 */
public abstract class AbstractPlayer implements Player {
  // both boards are of the same size, with the same number of ships
  protected ShipBoard board;
  protected Board opponentBoard;
  protected final Random rand;
  protected final List<Coord> prevSalvo;
  protected boolean isSetup;

  /**
   * Constructs a new AbstractPlayer, with random numbers generated by the given Random generator
   *
   * @param rand the random generator
   */
  public AbstractPlayer(Random rand) {
    // before setup is called, the board and opponent's board are both null
    this.board = null;
    this.opponentBoard = null;
    // to test random, this allows for seeding, like in Fundies 2
    this.rand = Objects.requireNonNull(rand);
    this.prevSalvo = new ArrayList<>();
    this.isSetup = false;
  }
  
  /**
   * Get the player's name.
   *
   * @return the player's name
   */
  @Override
  public abstract String name();
  
  /**
   * Given the specifications for a BattleSalvo board, return a list of ships with their locations
   * on the board, where ships are randomly placed.
   *
   * @param height         the height of the board, range: [6, 15] inclusive
   * @param width          the width of the board, range: [6, 15] inclusive
   * @param specifications a map of ship type to the number of occurrences each ship should
   *                       appear on the board
   * @return the placements of each ship on the board
   */
  @Override
  public List<Ship> setup(int height, int width, Map<ShipType, Integer> specifications) {
    this.board = new ShipBoard(height, width);
    this.opponentBoard = new Board(height, width);

    List<Ship> ships = new ArrayList<>();
    
    // ShipType is arranged from largest to smallest, 
    // so .values() returns largest first
    // (which need to be placed first to avoid invalid states)
    for (ShipType type : ShipType.values()) {
      // place the correct number of ships for each type
      for (int i = 0; i < specifications.get(type); i++) {
        ships.add(this.placeShip(type));
      }
    }

    this.isSetup = true;
    return ships;
  }

  /**
   * Places a ship of the given type onto this Player's board randomly
   *
   * @param type the ship of type to place
   * @return the ship that was placed
   */
  private Ship placeShip(ShipType type) {
    while (true) {
      // generate new coordinates randomly and determine if they are valid for the board
      // we don't have to check for the board being null,
      // since the private is only called after initializing
      int x = this.rand.nextInt(this.board.getWidth());
      int y = this.rand.nextInt(this.board.getHeight());
      ShipOrientation orientation =
          this.rand.nextBoolean() ? ShipOrientation.HORIZONTAL : ShipOrientation.VERTICAL;

      // check if it is valid
      if (this.board.isValidPlacement(new Coord(x, y), orientation, type)) {
        // then, place ship and return it, breaking out of the while loop
        return this.board.createAndPlaceShip(new Coord(x, y), orientation, type);
      }
    }
  }

  /**
   * Returns this player's shots on the opponent's board. The number of shots returned should
   * equal the number of ships on this player's board that have not sunk.
   *
   * @return the locations of shots on the opponent's board
   * @throws IllegalStateException if the board has not been set up
   */
  @Override
  public abstract List<Coord> takeShots();

  /**
   * Given the list of shots the opponent has fired on this player's board, report which
   * shots hit a ship on this player's board.
   *
   * @param opponentShotsOnBoard the opponent's shots on this player's board
   * @return a filtered list of the given shots that contain all locations of shots that hit a
   *         ship on this board
   * @throws IllegalStateException if the board has not been set up
   */
  @Override
  public List<Coord> reportDamage(List<Coord> opponentShotsOnBoard) {
    if (!this.isSetup) {
      throw new IllegalStateException("board has not been set up");
    }
    
    // reports the shots that hit, mutating the board with both hits and misses
    // receiveShot mutates the board and ships while filtering
    return opponentShotsOnBoard.stream().filter(c -> this.board.receiveShot(c)).toList();
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
    if (!this.isSetup) {
      throw new IllegalStateException("board has not been set up");
    }
    
    // compare with prevSalvo to determine what hit and what missed
    for (Coord c : this.prevSalvo) {
      if (shotsThatHitOpponentShips.contains(c)) {
        this.opponentBoard.setCellState(c, CellState.HIT);
      } else if (this.opponentBoard.getCellState(c) != CellState.HIT) {
        // to avoid un-marking duplicate shots, check to make sure it isn't HIT before making MISS
        this.opponentBoard.setCellState(c, CellState.MISS);
      }
    }
    // now that the previous salvo has been processed, we clear
    this.prevSalvo.clear();
  }

  /**
   * Notifies the player that the game is over.
   * Win, lose, and draw should all be supported.
   * Without networking, this method does nothing
   *
   * @param result if the player has won, lost, or forced a draw
   * @param reason the reason for the game ending
   */
  @Override
  public void endGame(GameResult result, String reason) {
    // as of current, this method serves no purpose
  }
}
