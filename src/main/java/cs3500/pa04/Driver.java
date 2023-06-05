package cs3500.pa04;

import cs3500.pa03.controller.ManualController;
import cs3500.pa03.model.AiPlayer;
import cs3500.pa03.model.HumanPlayer;
import cs3500.pa03.model.Player;
import cs3500.pa03.view.GameView;
import cs3500.pa03.view.SalvoView;
import java.io.InputStreamReader;

/**
 * This is the main driver of this project.
 */
public class Driver {
  /**
   * Project entry point
   *
   * @param args - no command line args required
   */
  public static void main(String[] args) {
    try {
      GameView view = new SalvoView(new InputStreamReader(System.in), System.out);
      Player human = new HumanPlayer(view);
      Player ai = new AiPlayer();
      new ManualController(human, ai, view).run();
    } catch (Exception e) {
      System.err.println("An unrecoverable error occurred");
    }
  }
}