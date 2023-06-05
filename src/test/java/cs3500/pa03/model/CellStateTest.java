package cs3500.pa03.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Represents a class to test CellState enum
 */
class CellStateTest {
  /**
   * Tests that CellStates are properly rendered using toString
   */
  @Test
  public void testCellState() {
    assertEquals("H", CellState.HIT.toString());
    assertEquals("M", CellState.MISS.toString());
    assertEquals("S", CellState.SHIP.toString());
    assertEquals(".", CellState.WATER.toString());
  }
}