package cs3500.pa03.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Represents a Ship placed on a ShipBoard in a game of BattleSalvo
 */
public class Ship {
  private final Coord originCoord;
  private final ShipOrientation orientation;
  // represents whether each of this Ship's segments have been hit
  private final boolean[] hitArray;

  /**
   * Constructs a new Ship with length derived from the ShipType at the given
   * Coord and in the given Orientation
   *
   * @param originCoord the coordinate of the top-left corner of the Ship
   * @param orientation the orientation of the Ship
   * @param type the type of the Ship
   */
  public Ship(Coord originCoord, ShipOrientation orientation, ShipType type) {
    this.originCoord = Objects.requireNonNull(originCoord);
    this.orientation = Objects.requireNonNull(orientation);
    // initially all false, meaning the ship has not been hit yet
    this.hitArray = new boolean[Objects.requireNonNull(type).getSize()];
  }

  /**
   * Returns whether this Ship is completely sunk
   *
   * @return whether this Ship is completely sunk
   */
  public boolean isSunk() {
    for (boolean isHit : this.hitArray) {
      if (!isHit) {
        // if any part of the ship is not hit, the ship is not sunk
        return false;
      }
    }
    return true;
  }

  /**
   * Receives a shot at the given coordinate, updating the internal hitArray
   * and returning true if the shot hits the ship, otherwise returning false.
   * Shooting an already-hit location will return false.
   *
   * @param shotCoord the Coord of the shot
   * @return whether the shot hits this Ship
   */
  public boolean receiveShot(Coord shotCoord) {
    if (!this.getOccupiedCoords().contains(shotCoord)) {
      return false;
    }
    
    // since we know the shot is on the ship, we can figure out where to index into hitArray
    // one of the two terms of the Math.max will always be zero, the other will be the index
    // we also know that shotCoord >= originCoord by the definition of origin
    int distFromOrigin = Math.max(shotCoord.x() - this.originCoord.x(), 
        shotCoord.y() - this.originCoord.y());
    
    // if this ship has been hit at the location before
    if (this.hitArray[distFromOrigin]) {
      return false;
    }
    
    this.hitArray[distFromOrigin] = true;
    return true;
  }

  /**
   * Returns the set of coordinates that this Ship occupies
   *
   * @return the set of coordinates that this Ship occupies
   */
  public Set<Coord> getOccupiedCoords() {
    Set<Coord> coords = new HashSet<>();
    
    int originX = this.originCoord.x();
    int originY = this.originCoord.y();
    
    for (int i = 0; i < this.hitArray.length; i++) {
      if (this.orientation == ShipOrientation.VERTICAL) {
        // vertical ship: add to y coordinate
        coords.add(new Coord(originX, originY + i));
      } else {
        // horizontal ship: add to x coordinate
        coords.add(new Coord(originX + i, originY));
      }
    }
    
    return coords;
  }
}
