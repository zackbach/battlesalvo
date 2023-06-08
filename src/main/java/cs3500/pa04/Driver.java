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
    if (args.length == 2) {
      // TODO: potentially alter ProxyController constructors?
      Driver.serverGame(args[0], Integer.parseInt(args[1]), GameType.SINGLE);
    } else if (args.length == 3) {
      // TODO: any validation or error checking here?
      Driver.serverGame(args[0], Integer.parseInt(args[1]), GameType.valueOf(args[2]));
    } else {
      Driver.localGame();
    }
  }
  
  private static void localGame() {
    try {
      GameView view = new SalvoView(new InputStreamReader(System.in), System.out);
      Player human = new HumanPlayer(view);
      Player ai = new AiPlayer();
      new ManualController(human, ai, view).run();
    } catch (Exception e) {
      System.err.println("An unrecoverable error occurred");
    }

  }
  
  // TODO: are we even allowed to do this? lol
  private static void serverGame(String host, int port, GameType type) {
    try (Socket server = new Socket(host, port)) {
      Player ai = new AiPlayer();
      new ProxyController(server, ai, type).run();
    } catch (IOException e) {
      System.err.println("Unable to connect to server");
    }
  }
}