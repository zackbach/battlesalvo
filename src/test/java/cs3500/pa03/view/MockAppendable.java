package cs3500.pa03.view;

import java.io.IOException;

/**
 * Represents a mock Appendable for testing IOExceptions
 */
class MockAppendable implements Appendable {
  private void throwInOut() throws IOException {
    throw new IOException("Mock throwing an error");
  }

  /**
   * Throws an exception for the purpose of testing
   *
   * @param csq
   *         The character sequence to append.  If {@code csq} is
   *         {@code null}, then the four characters {@code "null"} are
   *         appended to this Appendable.
   *
   * @return will never return, only throwing an IOException
   * @throws IOException whenever the method is called
   */
  @Override
  public Appendable append(CharSequence csq) throws IOException {
    this.throwInOut();
    return null;
  }

  /**
   * Throws an exception for the purpose of testing
   *
   * @param csq
   *         The character sequence from which a subsequence will be
   *         appended.  If {@code csq} is {@code null}, then characters
   *         will be appended as if {@code csq} contained the four
   *         characters {@code "null"}.
   *
   * @param start
   *         The index of the first character in the subsequence
   *
   * @param end
   *         The index of the character following the last character in the
   *         subsequence
   *
   * @return will never return, only throwing an IOException
   * @throws IOException whenever the method is called
   */
  @Override
  public Appendable append(CharSequence csq, int start, int end) throws IOException {
    this.throwInOut();
    return null;
  }

  /**
   * Throws an exception for the purpose of testing
   *
   * @param c
   *         The character to append
   *
   * @return will never return, only throwing an IOException
   * @throws IOException whenever the method is called
   */
  @Override
  public Appendable append(char c) throws IOException {
    this.throwInOut();
    return null;
  }
}