package cs3500.pa03.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Represents a Board with known ship information in a game of BattleSalvo
 */
public class ShipBoard extends Board {
  List<Ship> ships;

  /**
   * Constructs a new ShipBoard of the given height and width within the range [6, 15] inclusive
   * that is initially filled with WATER CellStates, along with an empty list of ships
   *
   * @param height the height of the board, in [6, 15] inclusive
   * @param width the width of the board, in [6, 15] inclusive
   * @throws IllegalArgumentException if height or width are not within the range [6, 15] inclusive
   */
  public ShipBoard(int height, int width) {
    super(height, width);
    this.ships = new ArrayList<>();
  }

  /**
   * Returns the number of unsunk ships on this ShipBoard
   *
   * @return the number of unsunk ships on this ShipBoard
   */
  public int getUnsunkShipTotal() {
    // you could use .count(), which maps to long and sums, but then you would have to cast
    return this.ships.stream().filter(Predicate.not(Ship::isSunk)).mapToInt(s -> 1).sum();
  }

  /**
   * Determines whether a Ship of the given orientation and type can be placed
   * at the given Coord on this ShipBoard
   *
   * @param coord the coordinate of the Ship's origin
   * @param orientation the Ship's orientation
   * @param type the Ship's type
   * @return whether a Ship could be placed
   */
  public boolean isValidPlacement(Coord coord, ShipOrientation orientation, ShipType type) {
    Ship toPlace = new Ship(coord, orientation, type);
    
    for (Coord c : toPlace.getOccupiedCoords()) {
      // checks if the coordinate is already occupied on the board
      if (this.isInvalidCoord(c) 
          || this.getCellState(c) == CellState.SHIP 
          || this.getCellState(c) == CellState.HIT) {
        return false;
      }
    }
    
    return true;
  }

  /**
   * Creates and returns a Ship of the given orientation and type placed
   * at the given Coord on this ShipBoard, if possible.
   *
   * @param coord the coordinate of the Ship's origin
   * @param orientation the Ship's orientation
   * @param type the Ship's type
   * @return the Ship with the given coord, orientation, and type
   * @throws IllegalArgumentException if the Ship cannot be placed
   */
  public Ship createAndPlaceShip(Coord coord, ShipOrientation orientation, ShipType type) {
    if (!this.isValidPlacement(coord, orientation, type)) {
      throw new IllegalArgumentException("ship cannot be placed here");
    }
    
    Ship toPlace = new Ship(coord, orientation, type);
    
    // update board to ensure that ships are always tracked correctly
    for (Coord c : toPlace.getOccupiedCoords()) {
      this.setCellState(c, CellState.SHIP);
    }
    
    this.ships.add(toPlace);
    return toPlace;
  }

  /**
   * Returns whether a shot fired at the given Coord hits an undamaged part of a ship.
   * If it does, the Board's state and Ship's state are updated appropriately
   *
   * @param coord the shot that a hit is made
   * @return whether the shot made at the given coord was a hit
   */
  public boolean receiveShot(Coord coord) {
    for (Ship s : this.ships) {
      // receiveShot will return false if it does not hit and true if it does, mutating the Ship
      if (s.receiveShot(coord)) {
        this.setCellState(coord, CellState.HIT);
        // we are safe to return now, since only one ship could be occupying
        return true;
      }
    }
    
    // it is possible to reach this point by firing a repeat shot, which we do not want to unmark
    if (this.getCellState(coord) != CellState.HIT) {
      this.setCellState(coord, CellState.MISS);
    }
    return false;
  }
}
