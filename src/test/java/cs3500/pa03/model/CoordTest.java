package cs3500.pa03.model;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class CoordTest {
  /**
   * Tests that coordinates with negative values cannot be constructed
   */
  @Test
  public void testCoordConstruction() {
    assertThrows(IllegalArgumentException.class, () -> new Coord(-1, -1));
    assertThrows(IllegalArgumentException.class, () -> new Coord(0, -1));
    assertThrows(IllegalArgumentException.class, () -> new Coord(-1, 0));
  }
}