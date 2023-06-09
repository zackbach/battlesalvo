package cs3500.pa03.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.junit.jupiter.api.Test;

class AiPlayerTest {
  
  /**
   * Tests that the setup receiving shots for an AI Player works as intended
   */
  @Test
  public void testAiPlayer() {
    Player ai = new AiPlayer();
    assertEquals("zackbach", ai.name());
    Map<ShipType, Integer> specs = new HashMap<>();
    specs.put(ShipType.CARRIER, 1);
    specs.put(ShipType.BATTLESHIP, 1);
    specs.put(ShipType.DESTROYER, 1);
    specs.put(ShipType.SUBMARINE, 1);
    
    List<Ship> ships = ai.setup(6, 6, specs);
    // get the coordinates of the first ship
    List<Coord> firstShipCoords = new ArrayList<>(ships.get(0).getOccupiedCoords());
    
    // sink the first ship: all are hits, so all should be returned
    assertEquals(firstShipCoords, ai.reportDamage(firstShipCoords));
    // the second time, the ship has already been sunk, so none should be hits
    assertEquals(new ArrayList<>(), ai.reportDamage(firstShipCoords));
    // and since a ship have been sunk, takeShots should return one fewer ship
    assertEquals(3, ai.takeShots().size());
    
    // sink the rest of the ships: collect all coordinates
    List<Coord> remainingCoords = new ArrayList<>();
    for (int i = 1; i < ships.size(); i++) {
      remainingCoords.addAll(ships.get(i).getOccupiedCoords());
    }
    
    // all are hits initially and not the second time
    assertEquals(remainingCoords, ai.reportDamage(remainingCoords));
    assertEquals(new ArrayList<>(), ai.reportDamage(remainingCoords));
    // all ships should now be sunk
    assertEquals(0, ai.takeShots().size());
  }

  /**
   * Tests that the AIPlayer can take shots on the opponents board with the given information
   */
  @Test
  public void testTakeShots() {
    // unlike above, use seeded random here
    Player ai = new AiPlayer(new Random(1));
    Map<ShipType, Integer> specs = new HashMap<>();
    specs.put(ShipType.CARRIER, 2);
    specs.put(ShipType.BATTLESHIP, 1);
    specs.put(ShipType.DESTROYER, 1);
    specs.put(ShipType.SUBMARINE, 1);
    ai.setup(6, 6, specs);
    
    // tests that takeShots generates a random list of shots among possible targets
    // since we use a seeded random, it will always be this list of shots
    List<Coord> shotsTaken1 = new ArrayList<>(Arrays.asList(new Coord(3, 2), new Coord(4, 2), 
        new Coord(0, 2), new Coord(2, 0), new Coord(3, 3)));

    assertEquals(shotsTaken1, ai.takeShots());
    // update some of those shots to be hits (3, 3) and some to be misses (everything else)
    ai.successfulHits(List.of(new Coord(3, 3)));

    // none of these shots taken will be the same as the shots taken above
    // note that the shots here are neighboring to (3, 3)
    // also, (3, 2) is not shot at, even though it is neighboring, since it has been hit before
    List<Coord> shotsTaken2 = new ArrayList<>(Arrays.asList(new Coord(4, 3), new Coord(3, 4),
        new Coord(2, 3), new Coord(1, 4), new Coord(4, 1)));
    
    assertEquals(shotsTaken2, ai.takeShots());
    // mark all of those shots as misses, to clear internal state
    ai.successfulHits(new ArrayList<>());
    
    // at this point, 10 / 36 shots have been taken. We will take 25 more
    for (int i = 0; i < 5; i++) {
      // 5 shots should be taken each time, since the AI has 5 ships remaining
      assertEquals(5, ai.takeShots().size());
      // mark them as misses, so they aren't fired at again
      ai.successfulHits(new ArrayList<>());
    }
    
    // at this point, there is only one coordinate left that has not been shot at
    // since we seeded the random generator, we know it will always be this coord:
    assertEquals(List.of(new Coord(4, 0)), ai.takeShots());
  }

  /**
   * Tests that before the AIPlayer has been set up, other methods cannot be called
   */
  @Test
  public void testSetupExceptions() {
    Player ai = new AiPlayer();
    
    assertThrows(IllegalStateException.class, ai::takeShots);
    assertThrows(IllegalStateException.class, () -> ai.reportDamage(List.of()));
    assertThrows(IllegalStateException.class, () -> ai.successfulHits(List.of()));

    Map<ShipType, Integer> specs = new HashMap<>();
    specs.put(ShipType.CARRIER, 2);
    specs.put(ShipType.BATTLESHIP, 1);
    specs.put(ShipType.DESTROYER, 1);
    specs.put(ShipType.SUBMARINE, 1);
    ai.setup(6, 6, specs);

    // after setup, these methods should not throw
    assertDoesNotThrow(ai::takeShots);
    assertDoesNotThrow(() -> ai.reportDamage(List.of()));
    assertDoesNotThrow(() -> ai.successfulHits(List.of()));
    assertDoesNotThrow(() -> ai.endGame(GameResult.WIN, "win"));
  }
}