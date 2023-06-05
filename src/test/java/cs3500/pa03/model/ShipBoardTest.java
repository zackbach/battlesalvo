package cs3500.pa03.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Represents a class to test ShipBoard
 */
class ShipBoardTest {
  /**
   * Tests ship placement and shooting logic
   */
  @Test
  public void testShipLogic() {
    ShipBoard sb = new ShipBoard(10, 10);
    
    // initially, there are no ships on the board
    assertEquals(0, sb.getUnsunkShipTotal());
    
    Coord loc = new Coord(1, 1);
    assertTrue(sb.isValidPlacement(loc, ShipOrientation.HORIZONTAL, ShipType.SUBMARINE));
    
    Ship s = sb.createAndPlaceShip(loc, ShipOrientation.HORIZONTAL, ShipType.SUBMARINE);
    assertFalse(s.isSunk());
    assertEquals(1, sb.getUnsunkShipTotal());
    // tests that the board was accurately updated
    assertEquals(CellState.SHIP, sb.getCellState(loc));
    assertEquals(CellState.SHIP, sb.getCellState(new Coord(2, 1)));
    assertEquals(CellState.SHIP, sb.getCellState(new Coord(3, 1)));

    // tests that ships can be fired at, and both the Ship and ShipBoard are updated
    assertTrue(sb.receiveShot(loc));
    assertEquals(CellState.HIT, sb.getCellState(loc));
    // since receiveShot on ship returns false if the location has already been hit,
    // we can check that the internal Ship's state has been updated
    assertFalse(s.receiveShot(loc));
    
    // tests that repeated shots are not registered as hits
    assertFalse(sb.receiveShot(loc));
    assertEquals(CellState.HIT, sb.getCellState(loc));
    
    // tests that shots at shipless locations are misses
    assertFalse(sb.receiveShot(new Coord(0, 0)));
    assertEquals(CellState.MISS, sb.getCellState(new Coord(0, 0)));

    // sinks the ship
    assertTrue(sb.receiveShot(new Coord(2, 1)));
    assertTrue(sb.receiveShot(new Coord(3, 1)));
    assertEquals(0, sb.getUnsunkShipTotal());
    assertTrue(s.isSunk());
  }

  /**
   * Tests ship placement logic at the edge of boards
   */
  @Test
  public void testBadShipPlacement() {
    ShipBoard smallBoard = new ShipBoard(6, 6);
    
    // tests that ships near the edge can be placed
    assertTrue(smallBoard.isValidPlacement(new Coord(0, 0), ShipOrientation.HORIZONTAL, 
        ShipType.CARRIER));
    
    // tests that ships cannot be placed off of the board
    assertFalse(smallBoard.isValidPlacement(new Coord(1, 0), ShipOrientation.HORIZONTAL, 
        ShipType.CARRIER));
    // and that attempting to place such a ship throws an exception
    assertThrows(IllegalArgumentException.class, 
        () -> smallBoard.createAndPlaceShip(new Coord(1, 0), ShipOrientation.HORIZONTAL, 
            ShipType.CARRIER));
    
    // tests that ships cannot be placed overlapping
    smallBoard.createAndPlaceShip(new Coord(0, 0), ShipOrientation.HORIZONTAL, ShipType.CARRIER);
    assertFalse(smallBoard.isValidPlacement(new Coord(0, 0), ShipOrientation.VERTICAL,
        ShipType.CARRIER));
    // even if the ship has been hit first
    smallBoard.receiveShot(new Coord(0, 0));
    assertEquals(CellState.HIT, smallBoard.getCellState(new Coord(0, 0)));
    assertFalse(smallBoard.isValidPlacement(new Coord(0, 0), ShipOrientation.VERTICAL,
        ShipType.CARRIER));
  }
}