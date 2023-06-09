package cs3500.pa04;

import cs3500.pa03.controller.ManualController;
import cs3500.pa03.model.AiPlayer;
import cs3500.pa03.model.HumanPlayer;
import cs3500.pa03.model.Player;
import cs3500.pa03.view.GameView;
import cs3500.pa03.view.SalvoView;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

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
    // if host and port are provided -> start a server game
    if (args.length == 2) {
      try (Socket server = new Socket(args[0], Integer.parseInt(args[1]))) {
        Player ai = new AiPlayer();
        new ProxyController(server, ai).run();
      } catch (IOException e) {
        System.err.println("Unable to connect to server");
      } catch (Exception e) {
        System.err.println("An error has occurred");
      }
      
    } else if (args.length == 0) {
      try {
        GameView view = new SalvoView(new InputStreamReader(System.in), System.out);
        Player human = new HumanPlayer(view);
        Player ai = new AiPlayer();
        
        new ManualController(human, ai, view).run();
      } catch (Exception e) {
        System.err.println("An error has occurred");
      }
      
    } else {
      System.err.println("Please enter a valid amount of command line arguments");
    }
  }
}