package com.v1ct04.spreadsheets.ui;

import com.v1ct04.spreadsheets.model.Spreadsheet;
import com.v1ct04.spreadsheets.model.SpreadsheetUtils;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * This is more of an integration test than a unit test since we are using the real instances of all the classes (no
 * mocks are being used), from Spreadsheet (provided) to the arithmetic parser (not even injected).
 */
public class CellDisplayValueTest {

  private Spreadsheet mSpreadsheet;
  private CellDisplayValueManager mDisplayValueManager;

  @Before
  public void setUp() {
    mSpreadsheet = new Spreadsheet();
    mDisplayValueManager = new CellDisplayValueManager(mSpreadsheet);
  }

  @Test
  public void testDisplayValueForCells() {
    assertEquals("", mDisplayValueManager.getDisplayValueOfCellAt(0, 0));
    mSpreadsheet.setCellContentAt(0, 0, "a");
    assertEquals("a", mDisplayValueManager.getDisplayValueOfCellAt(0, 0));
    mSpreadsheet.setCellContentAt(0, 0, "3");
    assertEquals(3.0, mDisplayValueManager.getDisplayValueOfCellAt(0, 0));
    mSpreadsheet.setCellContentAt(0, 0, "=1+1");
    assertEquals(2.0, mDisplayValueManager.getDisplayValueOfCellAt(0, 0));
  }

  @Test
  public void testDependentCells() {
    mSpreadsheet.setCellContentAt(SpreadsheetUtils.cellLocationForName("E1"), "4");
    mSpreadsheet.setCellContentAt(SpreadsheetUtils.cellLocationForName("G2"), "= 5+4/2"); // 7
    mSpreadsheet.setCellContentAt(0, 0, "=E1*G2+G2-E1");
    assertEquals(31.0, mDisplayValueManager.getDisplayValueOfCellAt(0, 0));
  }

  @Test
  public void testExpressionValueCache() {
    mSpreadsheet.setCellContentAt(SpreadsheetUtils.cellLocationForName("E1"), "2");
    mSpreadsheet.setCellContentAt(0, 0, "=E1");
    assertEquals(2.0, mDisplayValueManager.getDisplayValueOfCellAt(0, 0));
    mSpreadsheet.setCellContentAt(SpreadsheetUtils.cellLocationForName("E1"), "4");
    assertEquals(2.0, mDisplayValueManager.getDisplayValueOfCellAt(0, 0));
    mDisplayValueManager.invalidateCache();
    assertEquals(4.0, mDisplayValueManager.getDisplayValueOfCellAt(0, 0));
  }

  @Test
  public void testCyclicExpressions() {
    mSpreadsheet.setCellContentAt(SpreadsheetUtils.cellLocationForName("A1"), "= A2 + 1");
    mSpreadsheet.setCellContentAt(SpreadsheetUtils.cellLocationForName("A2"), "= A1 + 1");
    assertEquals("#!CYCLE", mDisplayValueManager.getDisplayValueOfCellAt(0, 0)); // A1 value
  }
}
