package com.v1ct04.spreadsheets.ui;

import com.v1ct04.arithmetic.ArithmeticExpressionParser;
import com.v1ct04.arithmetic.ArithmeticParseException;
import com.v1ct04.arithmetic.VariableValueProvider;
import com.v1ct04.spreadsheets.model.CellLocation;
import com.v1ct04.spreadsheets.model.Spreadsheet;
import com.v1ct04.spreadsheets.model.SpreadsheetUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This is the class responsible for deciding what to display for a certain cell on the table view. Will try to parse
 * the cell's formula, if there is one, using an arithmetic parser or try to read the number in the cell. Will also
 * handle identifying cycle dependencies between cells and have a cache of expression values so that we don't end up
 * calculating the same expression a dozen of times for the same spreadsheet configuration (one must call to invalidate
 * the cache when the spreadsheet data has changed though).
 */
public class CellDisplayValueManager {

  private static String CELL_FORMULA_PREFIX = "=";

  private final Spreadsheet mSpreadsheet;
  private final ArithmeticExpressionParser mExpressionParser;

  private Set<String> mCurrentOperationExpressions = new HashSet<>();
  private Map<String, Double> mCachedExpressionValues = new HashMap<>();

  public CellDisplayValueManager(Spreadsheet spreadsheet) {
    mSpreadsheet = spreadsheet;
    mExpressionParser = new ArithmeticExpressionParser();
    mExpressionParser.setVariableValueProvider(new SpreadsheetVariableValueProvider());
  }

  public Object getDisplayValueOfCellAt(CellLocation cellLocation) {
    return getDisplayValueOfCellAt(cellLocation.getColumn(), cellLocation.getRow());
  }

  public Object getDisplayValueOfCellAt(int column, int row) {
    String cellString = mSpreadsheet.getCellContentAt(column, row);
    if (cellString == null) {
      return "";
    } else if (cellString.startsWith(CELL_FORMULA_PREFIX)) {
      try {
        String cellExpression = cellString.substring(CELL_FORMULA_PREFIX.length());
        return parseCellExpression(cellExpression);
      } catch (ArithmeticParseException e) {
        return e.getDisplayErrorCode();
      }
    } else {
      try {
        return Double.parseDouble(cellString);
      } catch (NumberFormatException ex) {
        return cellString;
      }
    }
  }

  public void invalidateCache() {
    mCachedExpressionValues.clear();
  }

  /**
   *  Will call an arithmetic expression parser to evaluate the expression. This is mostly for detecting cycles in
   *  cells dependencies (that's why this method is synchronized) and for caching expression results.
   */
  private synchronized double parseCellExpression(String expression) throws ArithmeticParseException {
    if (mCurrentOperationExpressions.contains(expression)) {
      throw new ArithmeticParseException("Spreadsheet contains cycles", "#!CYCLE", 0);
    }
    mCurrentOperationExpressions.add(expression);
    try {
      Double result = mCachedExpressionValues.get(expression);
      if (result == null) {
        result = mExpressionParser.evaluate(expression);
        mCachedExpressionValues.put(expression, result);
      }
      return result;
    } finally {
      mCurrentOperationExpressions.remove(expression);
    }
  }

  private double getDoubleValueOfCellContent(String cellContent) throws ArithmeticParseException {
    if (cellContent == null) {
      return 0;
    } else if (cellContent.startsWith(CELL_FORMULA_PREFIX)) {
      String cellExpression = cellContent.substring(CELL_FORMULA_PREFIX.length());
      return parseCellExpression(cellExpression);
    } else {
      try {
        return Double.parseDouble(cellContent);
      } catch (NumberFormatException ex) {
        return Double.NaN;
      }
    }
  }

  private class SpreadsheetVariableValueProvider implements VariableValueProvider {

    @Override
    public double getValueOf(String name) throws ArithmeticParseException {
      CellLocation location;
      try {
        location = SpreadsheetUtils.cellLocationForName(name);
      } catch (IllegalArgumentException ex) {
        throw new ArithmeticParseException("Invalid cell name", "#!INVLD");
      }
      return getDoubleValueOfCellContent(mSpreadsheet.getCellContentAt(location));
    }
  }
}
