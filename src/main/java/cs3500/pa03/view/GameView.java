package cs3500.pa03.view;

import java.util.List;

/**
 * Represents the View for the BattleSalvo game that displays output
 */
public interface GameView {

  /**
   * Displays the given message followed by a newline
   *
   * @param message the message to be displayed
   * @throws RuntimeException if there is an IO error
   */
  void displayMessage(String message);

  /**
   * Receives a specified number of integers from the user
   *
   * @param n the number of integers to return
   * @param retry the message displayed if more/fewer than n ints are inputted
   * @return a list containing the integers inputted
   */
  List<Integer> getSomeInts(int n, String retry);
}
