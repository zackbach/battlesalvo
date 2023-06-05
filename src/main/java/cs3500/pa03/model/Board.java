package cs3500.pa03.model;

import java.util.Arrays;

/**
 * Represents a board without known Ship information in a game of BattleSalvo
 */
public class Board {
  protected final CellState[][] board;

  /**
   * Constructs a new Board of the given height and width within the range [6, 15] inclusive
   * that is initially filled with WATER CellStates
   *
   * @param height the (positive) height of the board, in [6, 15] inclusive
   * @param width the (positive) width of the board, in [6, 15] inclusive
   * @throws IllegalArgumentException if height or width are not within the range [6, 15] inclusive
   */
  public Board(int height, int width) {
    // As per Piazza @773_f4, the model "owns" what a correct input is
    // After speaking with Dr. F, this was the approach he suggested
    if (height < BattleSalvoConfig.MIN_HEIGHT || height > BattleSalvoConfig.MAX_HEIGHT 
        || width < BattleSalvoConfig.MIN_WIDTH || width > BattleSalvoConfig.MAX_WIDTH) {
      throw new IllegalArgumentException("board dimensions must be within the range [6, 15]");
    }
    this.board = new CellState[height][width];
    
    // fill with water tiles initially
    for (CellState[] cellStates : this.board) {
      Arrays.fill(cellStates, CellState.WATER);
    }
  }

  /**
   * Returns the height of this Board
   *
   * @return the height of this Board
   */
  public int getHeight() {
    return this.board.length;
  }

  /**
   * Returns the width of this Board
   *
   * @return the width of this Board
   */
  public int getWidth() {
    return this.board[0].length;
  }

  /**
   * Returns the CellState at the given Coord, if possible
   *
   * @param coord the coordinate to get the CellState at, 
   *              where the top left corner of the board is (0, 0)
   * @return the CellState at the given Coord, if possible
   * @throws IllegalArgumentException if the Coord is not in this Board's bounds
   */
  public CellState getCellState(Coord coord) {
    if (this.isInvalidCoord(coord)) {
      throw new IllegalArgumentException("given coordinate is not in the bounds of the board");
    }
    
    // we have to have y come first, since it is the row, then x (column)
    return this.board[coord.y()][coord.x()];
  }

  /**
   * Sets the CellState at the given Coord to be the newState, if possible
   *
   * @param coord the coordinate to get the CellState at, 
   *              where the top left corner of the board is (0, 0)
   * @param newState the CellState to set the board cell at the given Coord to
   * @throws IllegalArgumentException if the Coord is not in this Board's bounds
   */
  public void setCellState(Coord coord, CellState newState) {
    if (this.isInvalidCoord(coord)) {
      throw new IllegalArgumentException("given coordinate is not in the bounds of the board");
    }

    // we have to have y come first, since it is the row, then x (column)
    this.board[coord.y()][coord.x()] = newState;
  }

  /**
   * Determines whether the given coordinate is out of bounds for this Board
   *
   * @param coord the given coordinate
   * @return whether the given coordinate is out of bounds for this Board
   */
  public boolean isInvalidCoord(Coord coord) {
    // we do not have to check for negative Coords, since they cannot be constructed
    return coord.y() >= this.getHeight() || coord.x() >= this.getWidth();
  }
  
  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();
    
    for (CellState[] row : this.board) {
      StringBuilder rowString = new StringBuilder();
      for (CellState cs : row) {
        rowString.append(cs).append(" ");
      }
      result.append(rowString.toString().trim()).append(System.lineSeparator());
    }
    
    return result.toString().trim();
  }
}
