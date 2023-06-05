package cs3500.pa03.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import cs3500.pa03.model.AiPlayer;
import cs3500.pa03.model.HumanPlayer;
import cs3500.pa03.model.Player;
import cs3500.pa03.view.GameView;
import cs3500.pa03.view.SalvoView;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;
import org.junit.jupiter.api.Test;

class ManualControllerTest {
  /**
   * Tests game between a human player and an AI where the player wins
   */
  @Test
  public void testHumanGame() {
    // 6 x 6 board, with one of each ship. Then, hits exactly all the AI's ships
    // (and two more to round out a full volley)
    Readable in = new StringReader("6 6\n 1 1 1 1\n0 0\n0 1\n0 2\n0 3\n1 2\n1 3\n1 4\n"
        + "2 1\n2 2\n2 3\n2 4\n2 5\n3 0\n3 1\n3 2\n3 3\n3 4\n3 5\n4 0\n0 0");
    Appendable out = new StringBuilder();
    GameView view = new SalvoView(in, out);
    // human and AI have same ships, so it's easy to hit
    Player human = new HumanPlayer(view, new Random(1));
    Player ai = new AiPlayer(new Random(1));
    
    new ManualController(human, ai, view).run();
    
    try {
      // file contains expected output to make test more readable
      assertEquals(Files.readString(Path.of("src", "test", "resources", "player-game-output.txt")),
          out.toString());
    } catch (IOException e) {
      fail();
    }
  }
  
  /**
   * Tests setup edge cases, as well as a game between two AI players ending in a tie
   */
  @Test
  public void testAutomaticTieGame() {
    // various bad inputs are tested here, to make sure that bad inputs are handled appropriately
    // a bunch of bad coordinates (tests all possibilities) are followed by bad fleet sizes
    Readable in = new StringReader("0 0\n5 20\n20 5\n20 20\n5 6\n6 5\n20 6\n6 20\n6 6\n"
        + "0 0 0 0\n1 1 1 0\n4 4 4 4\n1 1 1 1");
    Appendable out = new StringBuilder();
    GameView view = new SalvoView(in, out);
    // since the two AIs have the same seeding, the output will be a tie.
    Player player1 = new AiPlayer(new Random(1));
    Player player2 = new AiPlayer(new Random(1));

    new ManualController(player1, player2, view).run();

    try {
      // file contains expected output to make test more readable
      assertEquals(Files.readString(Path.of("src", "test", "resources", "tie-game-output.txt")),
          out.toString());
    } catch (IOException e) {
      fail();
    }
  }

  /**
   * Tests a game between the "player" (an AI) and the opponent where the opponent wins
   */
  @Test
  public void testAutomaticLoseGame() {
    Readable in = new StringReader("6 6\n 1 1 1 1");
    Appendable out = new StringBuilder();
    GameView view = new SalvoView(in, out);
    Player player1 = new AiPlayer(new Random(1));
    Player player2 = new AiPlayer(new Random(2));

    // here, player2 wins. since player1 was sent as the "player" and player2 as the "opponent",
    // a "you lose" message will be sent.
    new ManualController(player1, player2, view).run();

    try {
      // file contains expected output to make test more readable
      assertEquals(Files.readString(Path.of("src", "test", "resources", "sample-game-output.txt")),
          out.toString());
    } catch (IOException e) {
      fail();
    }

  }
}