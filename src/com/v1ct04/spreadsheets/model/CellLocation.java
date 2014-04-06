package com.v1ct04.spreadsheets.model;

public class CellLocation implements Comparable<CellLocation> {

  private int mColumn;
  private int mRow;

  public CellLocation(int column, int row) {
    mColumn = column;
    mRow = row;
  }

  public int getColumn() {
    return mColumn;
  }

  public int getRow() {
    return mRow;
  }

  @Override
  public int compareTo(CellLocation other) {
    int cmp = Integer.compare(mColumn, other.mColumn);
    if (cmp != 0) return cmp;
    return Integer.compare(mRow, other.mRow);
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    CellLocation that = (CellLocation) o;
    return mColumn == that.mColumn && mRow == that.mRow;
  }

  @Override
  public String toString() {
    return "CellLocation {" + mColumn + ", " + mRow + '}';
  }
}
