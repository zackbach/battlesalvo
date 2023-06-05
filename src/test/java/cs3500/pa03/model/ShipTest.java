package cs3500.pa03.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ShipTest {

  /**
   * Tests that a Ship can return its occupied coordinates as intended
   */
  @Test
  public void testOccupiedCoords() {
    // horizontal ship of size 3
    Ship s1 = new Ship(new Coord(0, 0), ShipOrientation.HORIZONTAL, ShipType.SUBMARINE);
    assertFalse(s1.isSunk());
    Set<Coord> s1Set = new HashSet<>();
    s1Set.add(new Coord(0, 0));
    s1Set.add(new Coord(1, 0));
    s1Set.add(new Coord(2, 0));

    // tests that the occupied coordinates are exactly (0, 0), (1, 0), and (2, 0)
    assertEquals(s1Set, s1.getOccupiedCoords());
    
    // tests that a vertically placed ship can return its occupied coordinates
    Ship s2 = new Ship(new Coord(1, 1), ShipOrientation.VERTICAL, ShipType.DESTROYER);
    assertFalse(s2.isSunk());
    Set<Coord> s2Set = new HashSet<>();
    s2Set.add(new Coord(1, 1));
    s2Set.add(new Coord(1, 2));
    s2Set.add(new Coord(1, 3));
    s2Set.add(new Coord(1, 4));

    assertEquals(s2Set, s2.getOccupiedCoords());
  }

  /**
   * Tests that a ship can be hit and sunk
   */
  @Test
  public void testHits() {
    // horizontal ship of size 3: occupies (0, 0), (1, 0), (2, 0)
    Ship s1 = new Ship(new Coord(0, 0), ShipOrientation.HORIZONTAL, ShipType.SUBMARINE);
    
    assertFalse(s1.isSunk());

    // tests that shots that do not hit the ship return false
    assertFalse(s1.receiveShot(new Coord(5, 5)));
    assertFalse(s1.receiveShot(new Coord(3, 0)));

    // takes the shot, which should return true
    assertTrue(s1.receiveShot(new Coord(0, 0)));
    assertFalse(s1.isSunk());
    
    assertTrue(s1.receiveShot(new Coord(1, 0)));
    assertFalse(s1.isSunk());

    assertTrue(s1.receiveShot(new Coord(2, 0)));
    // at this point, the ship should be sunk
    assertTrue(s1.isSunk());
    
    // tests that repeat hits at locations return false
    assertFalse(s1.receiveShot(new Coord(2, 0)));
  }
}