package cs3500.pa03.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Represents a class to test ShipType enum
 */
class ShipTypeTest {
  /**
   * Tests the possible ShipTypes and that their sizes correspond to the spec
   */
  @Test
  public void testShipType() {
    assertEquals(3, ShipType.SUBMARINE.getSize());
    assertEquals(4, ShipType.DESTROYER.getSize());
    assertEquals(5, ShipType.BATTLESHIP.getSize());
    assertEquals(6, ShipType.CARRIER.getSize());
  }
}