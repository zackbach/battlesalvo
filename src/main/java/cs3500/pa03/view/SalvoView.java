package cs3500.pa03.view;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * Represents the View for the BattleSalvo game that displays output
 */
public class SalvoView implements GameView {
  Scanner in;
  Appendable out;

  /**
   * Constructs a new GameView that appends output to the given Appendable
   *
   * @param in the Readable input source
   * @param out the Appendable output source
   */
  public SalvoView(Readable in, Appendable out) {
    this.in = new Scanner(in);
    this.out = out;
  }

  /**
   * Displays the given message with a newline by appending to this GameView's Appendable
   *
   * @param message the message to be displayed
   * @throws RuntimeException if the Appendable cannot be appended to
   */
  public void displayMessage(String message) {
    try {
      this.out.append(String.format("%s%n", message));
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage());
    }
  }

  /**
   * Receives a specified number of integers from the user
   *
   * @param n the number of integers to return
   * @param retry the message displayed if more/fewer than n ints are inputted
   * @return a list containing the integers inputted
   */
  public List<Integer> getSomeInts(int n, String retry) {
    while (true) {
      List<Integer> ints = new ArrayList<>();
      // reads line from the nextLine
      StringTokenizer st = new StringTokenizer(this.in.nextLine());
      while (st.hasMoreTokens()) {
        try {
          ints.add(Integer.parseInt(st.nextToken()));
        } catch (NumberFormatException e) {
          break;
        }
      }
      
      if (ints.size() != n || st.hasMoreTokens()) {
        // if there were too many/few integers, or we had to exit early
        this.displayMessage(retry);
      } else {
        return ints;
      }
    }
  }
}
