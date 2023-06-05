package cs3500.pa03.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Represents a class to test Board enum
 */
class BoardTest {
  /**
   * Tests the constructor and getters for Board
   */
  @Test
  public void testConstructor() {
    // tests that boards that are too small or too large cannot be constructed
    assertThrows(IllegalArgumentException.class, () -> new Board(5, 5));
    assertThrows(IllegalArgumentException.class, () -> new Board(16, 16));
    // tests that boards with one illegal argument still cannot be constructed
    assertThrows(IllegalArgumentException.class, () -> new Board(10, 5));
    assertThrows(IllegalArgumentException.class, () -> new Board(10, 20));
    
    Board b = new Board(10, 7);
    assertEquals(10, b.getHeight());
    assertEquals(7, b.getWidth());
    
    // tests that the board is initialized to be WATER
    for (int x = 0; x < b.getWidth(); x++) {
      for (int y = 0; y < b.getHeight(); y++) {
        assertEquals(CellState.WATER, b.getCellState(new Coord(x, y)));
      }
    }
    
    // tests that invalid coordinates are based on the board's actual coordinates, 
    // not the possible coordinates [6, 15]
    assertThrows(IllegalArgumentException.class, () -> b.getCellState(new Coord(0, 11)));
    assertThrows(IllegalArgumentException.class, () -> b.getCellState(new Coord(11, 0)));
    // tests that coordinates near the border are correctly rejected (out of bounds)
    assertThrows(IllegalArgumentException.class, () -> b.getCellState(new Coord(7, 0)));
    assertThrows(IllegalArgumentException.class, () -> b.getCellState(new Coord(0, 10)));
  }

  /**
   * Tests the setters and toString for Board
   */
  @Test
  public void testMutation() {
    Board b = new Board(6, 6);
    
    String result = String.format(". . . . . .%n. . . . . .%n. . . . . .%n. . . . . ."
        + "%n. . . . . .%n. . . . . .");
    
    // tests that initially, the board's string is as expected
    // above we test that it is constructed to be all water CellStates
    assertEquals(result, b.toString());
    
    Coord topRight = new Coord(5, 0);
    b.setCellState(topRight, CellState.HIT);
    assertEquals(b.getCellState(topRight), CellState.HIT);
    
    String mutated = String.format(". . . . . H%n. . . . . .%n. . . . . .%n. . . . . ."
        + "%n. . . . . .%n. . . . . .");

    // tests that initially, the board's string is as expected
    // above we test that it is constructed to be all water CellStates
    assertEquals(mutated, b.toString());
    
    // tests that we cannot set out-of-bounds cell states
    // note that more bounds checking logic is checked above with the getter
    assertThrows(IllegalArgumentException.class, 
        () -> b.setCellState(new Coord(10, 5), CellState.MISS));
  }
}