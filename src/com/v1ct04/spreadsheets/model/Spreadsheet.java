package com.v1ct04.spreadsheets.model;

import java.util.Collection;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;

public class Spreadsheet {

  SortedMap<CellLocation, String> mCellData = new TreeMap<>();

  public String getCellContentAt(int column, int row) {
    return getCellContentAt(new CellLocation(column, row));
  }

  public String getCellContentAt(CellLocation location) {
    return mCellData.get(location);
  }

  public boolean setCellContentAt(int column, int row, String value) {
    return setCellContentAt(new CellLocation(column, row), value);
  }

  public boolean setCellContentAt(CellLocation location, String value) {
    if (value != null && value.length() > 0) {
      String oldValue = mCellData.put(location, value);
      return !value.equals(oldValue);
    } else {
      return (mCellData.remove(location) != null);
    }
  }

  public int columnCount() {
    if (mCellData.isEmpty()) {
      return 0;
    } else {
      return mCellData.lastKey().getColumn() + 1;
    }
  }

  public int rowCount() {
    if (mCellData.isEmpty()) {
      return 0;
    } else {
      int maxRows = 0;
      for (CellLocation cellLocation : mCellData.keySet()) {
        maxRows = Math.max(maxRows, cellLocation.getRow());
      }
      return maxRows + 1;
    }
  }

  public Collection<CellLocation> getOccupiedLocations() {
    return Collections.unmodifiableCollection(mCellData.keySet());
  }
}
