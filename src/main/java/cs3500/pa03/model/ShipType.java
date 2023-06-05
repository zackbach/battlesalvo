package cs3500.pa03.model;

/**
 * Represents a type of Ship in a BattleSalvo game
 */
public enum ShipType {
  CARRIER(6),
  BATTLESHIP(5),
  DESTROYER(4),
  SUBMARINE(3);
  
  private final int size;

  /**
   * Constructs a ShipType with the given size
   *
   * @param size the size of the ShipType
   */
  ShipType(int size) {
    this.size = size;
  }

  /**
   * Returns the size of this ShipType
   *
   * @return the size of this ShipType
   */
  public int getSize() {
    return this.size;
  }
}
