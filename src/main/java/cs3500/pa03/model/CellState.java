package cs3500.pa03.model;

/**
 * Represents the state of a cell on a Board in BattleSalvo
 */
public enum CellState {
  WATER("."),
  SHIP("S"),
  HIT("H"),
  MISS("M");
  
  private final String renderAs;

  /**
   * Constructs a new CellState with the given renderAs string
   *
   * @param renderAs how the CellState should be rendered on a Board
   */
  CellState(String renderAs) {
    this.renderAs = renderAs;
  }

  /**
   * Returns the renderAs version of this CellState
   *
   * @return the shortened version of this CellState
   */
  @Override
  public String toString() {
    return this.renderAs;
  }
}
