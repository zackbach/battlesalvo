package cs3500.pa03.model;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import cs3500.pa03.view.GameView;
import cs3500.pa03.view.SalvoView;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.junit.jupiter.api.Test;

/**
 * Represents a class to test HumanPlayer
 */
class HumanPlayerTest {
  /**
   * Tests that the player can be constructed and takeShots
   */
  @Test
  public void testHumanShots() {
    // note that the view is tested thoroughly in SalvoViewTest,
    // so we only give ints (to check the part that the HumanPlayer handles)
    Readable in = new StringReader("0 0\n1 0\n2 0\n3 0\n-1 -1\n7 7\n1 6\n1 -1\n0 1\n1 1\n2 1\n3 1");
    Appendable out = new StringBuilder();
    GameView view = new SalvoView(in, out);
    
    Map<ShipType, Integer> specs = new HashMap<>();
    for (ShipType t : ShipType.values()) {
      specs.put(t, 1); // one of each ship type
    }
    
    // seeded random to test board string printed by takeShots
    Player human = new HumanPlayer(view, new Random(1));
    // setup and other player behavior (inherited from AbstractPlayer) 
    // is tested thoroughly in AiPlayer
    human.setup(6, 6, specs);
    assertEquals("Human Player", human.name());

    // tests that the shots are as expected
    assertEquals(List.of(new Coord(0, 0), new Coord(1, 0), new Coord(2, 0), new Coord(3, 0)), 
        human.takeShots());
    // tests that output is as expected
    String shotOutput = String.format("Opponent's Board:%n"
        + ". . . . . .%n. . . . . .%n. . . . . .%n. . . . . .%n. . . . . .%n. . . . . .%n%n"
        + "Your Board:%n"
        + "S . . S . .%nS . S S . .%nS S S S . .%nS S S S . .%n. S S S . .%n. . S S . .%n%n"
        + "Please enter 4 shots:%n");

    assertEquals(shotOutput, out.toString());
    
    // mark some of those shots as hits and others as misses
    human.reportDamage(List.of(new Coord(0, 0), new Coord(1, 0)));
    human.successfulHits(List.of(new Coord(0, 0), new Coord(1, 0)));
    
    // prompt another volley, this time with errors (see input above)
    assertEquals(List.of(new Coord(0, 1), new Coord(1, 1), new Coord(2, 1), new Coord(3, 1)),
        human.takeShots());
    // both boards should be updated with the hits and misses from above
    shotOutput += String.format("Opponent's Board:%n"
        + "H H M M . .%n. . . . . .%n. . . . . .%n. . . . . .%n. . . . . .%n. . . . . .%n%n"
        + "Your Board:%n"
        + "H M . S . .%nS . S S . .%nS S S S . .%nS S S S . .%n. S S S . .%n. . S S . .%n%n"
        + "Please enter 4 shots:%n"
        // since we had four bad shots, it prompts to retry twice
        + "Please enter coordinates that are within the board's dimensions%n"
        + "Please enter coordinates that are within the board's dimensions%n"
        + "Please enter coordinates that are within the board's dimensions%n"
        + "Please enter coordinates that are within the board's dimensions%n");
    assertEquals(shotOutput, out.toString());
  }

  /**
   * Tests that before the HumanPlayer has been set up, other methods cannot be called
   */
  @Test
  public void testSetupExceptions() {
    Player player = new HumanPlayer(new SalvoView(new StringReader(""), new StringBuilder()));

    assertThrows(IllegalStateException.class, player::takeShots);
    assertThrows(IllegalStateException.class, () -> player.reportDamage(List.of()));
    assertThrows(IllegalStateException.class, () -> player.successfulHits(List.of()));
    assertThrows(IllegalStateException.class, () -> player.endGame(GameResult.WIN, "win"));

    Map<ShipType, Integer> specs = new HashMap<>();
    specs.put(ShipType.CARRIER, 2);
    specs.put(ShipType.BATTLESHIP, 1);
    specs.put(ShipType.DESTROYER, 1);
    specs.put(ShipType.SUBMARINE, 1);
    player.setup(6, 6, specs);

    // after setup, these methods should not throw
    assertDoesNotThrow(() -> player.reportDamage(List.of()));
    assertDoesNotThrow(() -> player.successfulHits(List.of()));
    assertDoesNotThrow(() -> player.endGame(GameResult.WIN, "win"));
  }
}