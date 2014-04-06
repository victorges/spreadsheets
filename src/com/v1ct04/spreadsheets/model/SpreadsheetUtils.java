package com.v1ct04.spreadsheets.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpreadsheetUtils {

  public static final Pattern CELL_NAME_PATTERN = Pattern.compile("^[A-Z]+\\d+$");
  public static final Pattern COLUMN_NAME_PATTERN = Pattern.compile("[A-Z]+");

  public static String columnNameForIndex(int index) {
    String result = "";
    while (index >= 0) {
      char current = (char)('A' + index % 26);
      result = current + result;
      index = index / 26 - 1;
    }
    return result;
  }

  public static int columnIndexForName(String columnName) throws IllegalArgumentException {
    if (!COLUMN_NAME_PATTERN.matcher(columnName).matches()) {
      throw new IllegalArgumentException("Invalid column name: " + columnName);
    }
    int index = -1;
    for (char c : columnName.toCharArray()) {
      index = (index + 1) * 26 + (c - 'A');
    }
    return index;
  }

  public static CellLocation cellLocationForName(String cellName) throws IllegalArgumentException {
    if (!CELL_NAME_PATTERN.matcher(cellName).matches()) {
      throw new IllegalArgumentException("Invalid cell name: " + cellName);
    }
    Matcher columnMatcher = COLUMN_NAME_PATTERN.matcher(cellName);
    columnMatcher.find();

    String columnName = cellName.substring(columnMatcher.start(), columnMatcher.end());
    String rowString = cellName.substring(columnMatcher.end());
    int column = SpreadsheetUtils.columnIndexForName(columnName);
    int row = Integer.parseInt(rowString) - 1;

    return new CellLocation(column, row);
  }

  public static String cellNameForLocation(CellLocation cellLocation) {
    return cellNameForLocation(cellLocation.getColumn(), cellLocation.getRow());
  }

  public static String cellNameForLocation(int column, int row) {
    return columnNameForIndex(column) + (row + 1);
  }
}
