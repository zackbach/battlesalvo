package cs3500.pa03.view;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.StringReader;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Represents a class to test SalvoView
 */
class SalvoViewTest {
  /**
   * Tests that the view can output information
   */
  @Test
  public void testOutput() {
    Readable in = new StringReader("");
    Appendable out = new StringBuilder();
    GameView view = new SalvoView(in, out);
    
    view.displayMessage("Hello");
    view.displayMessage("World");
    // tests that messages are displayed, with newlines inserted
    assertEquals(String.format("Hello%nWorld%n"), out.toString());
    
    // tests IO exception behavior
    Readable inBad = new StringReader("");
    Appendable outBad = new MockAppendable();
    GameView viewBad = new SalvoView(inBad, outBad);
    
    assertThrows(RuntimeException.class, () -> viewBad.displayMessage("Error"));
  }

  /**
   * Tests that the view can receive and parse input
   */
  @Test
  public void testInput() {
    Readable inGood = new StringReader("1 2");
    Appendable outGood = new StringBuilder();
    GameView viewGood = new SalvoView(inGood, outGood);
    
    // tests that exactly two ints are received
    assertEquals(List.of(1, 2), viewGood.getSomeInts(2, "Retry"));
    // tests that no retry message was printed, since ints were good
    assertEquals("", outGood.toString());
    
    
    Readable inNotInt = new StringReader("s 2\n3 4");
    Appendable outNotInt = new StringBuilder();
    GameView viewNotInt = new SalvoView(inNotInt, outNotInt);

    // tests that the two ints were successfully received
    assertEquals(List.of(3, 4), viewNotInt.getSomeInts(2, "Retry"));
    // but the retry message was printed
    assertEquals(String.format("Retry%n"), outNotInt.toString());

    
    Readable inWrongNumber = new StringReader("1\n2 3 4\n5 6");
    Appendable outWrongNumber = new StringBuilder();
    GameView viewWrongNumber = new SalvoView(inWrongNumber, outWrongNumber);

    // tests that the two ints were successfully received
    assertEquals(List.of(5, 6), viewWrongNumber.getSomeInts(2, "Retry"));
    // but the retry message was printed twice, since the wrong number of ints were inputted
    assertEquals(String.format("Retry%nRetry%n"), outWrongNumber.toString());

    
    Readable inExtraStr = new StringReader("1 2 s 5\n3 4");
    Appendable outExtraStr = new StringBuilder();
    GameView viewExtraStr = new SalvoView(inExtraStr, outExtraStr);

    // tests that the two ints were successfully received
    // and the first input was ignored due to extra at the end
    assertEquals(List.of(3, 4), viewExtraStr.getSomeInts(2, "Retry"));
    assertEquals(String.format("Retry%n"), outExtraStr.toString());
  }
}