package cs3500.pa03.model;

/**
 * Represents a non-negative Coordinate
 */
public record Coord(int x, int y) {
  /**
   * Constructs a Coord with the given (non-negative) x and y
   *
   * @param x the x component of this Coord
   * @param y the y component of this Coord
   * @throws IllegalArgumentException if x or y are non-negative
   */
  public Coord {
    // note that you can have a nice clean constructor using the Record-specific syntax
    if (x < 0 || y < 0) {
      throw new IllegalArgumentException("Coords must be non-negative");
    }
  }
}
