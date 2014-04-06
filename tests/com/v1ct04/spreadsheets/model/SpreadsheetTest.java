package com.v1ct04.spreadsheets.model;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class SpreadsheetTest {

  private Spreadsheet mSpreadsheet;

  @Before
  public void setUp() {
    mSpreadsheet = new Spreadsheet();
  }

  @Test
  public void testNullEmptyValue() {
    assertNull(mSpreadsheet.getCellContentAt(0, 0));
    assertNull(mSpreadsheet.getCellContentAt(2, 4));
    assertNull(mSpreadsheet.getCellContentAt(3, 1));
    assertNull(mSpreadsheet.getCellContentAt(6, 0));
  }

  @Test
  public void testValueClearedIfEmpty() {
    mSpreadsheet.setCellContentAt(0, 0, "a");
    assertNotNull(mSpreadsheet.getCellContentAt(0, 0));
    mSpreadsheet.setCellContentAt(0, 0, "");
    assertNull(mSpreadsheet.getCellContentAt(0, 0));
  }

  @Test
  public void testCellChangedReturnValue() {
    assertTrue(mSpreadsheet.setCellContentAt(0, 0, "a"));
    assertFalse(mSpreadsheet.setCellContentAt(0, 0, "a"));
    assertTrue(mSpreadsheet.setCellContentAt(0, 0, "b"));
    assertFalse(mSpreadsheet.setCellContentAt(0, 0, "b"));
  }

  @Test
  public void testColumnCount() {
    assertEquals(0, mSpreadsheet.columnCount());
    mSpreadsheet.setCellContentAt(0, 0, "a");
    assertEquals(1, mSpreadsheet.columnCount());
    mSpreadsheet.setCellContentAt(1, 9, "b");
    assertEquals(2, mSpreadsheet.columnCount());
    mSpreadsheet.setCellContentAt(5, 5, "d");
    assertEquals(6, mSpreadsheet.columnCount());
    mSpreadsheet.setCellContentAt(4, 8, "e");
    assertEquals(6, mSpreadsheet.columnCount());
    mSpreadsheet.setCellContentAt(5, 5, null);
    assertEquals(5, mSpreadsheet.columnCount());
  }

  @Test
  public void testRowCount() {
    assertEquals(0, mSpreadsheet.rowCount());
    mSpreadsheet.setCellContentAt(0, 0, "a");
    assertEquals(1, mSpreadsheet.rowCount());
    mSpreadsheet.setCellContentAt(1, 9, "b");
    assertEquals(10, mSpreadsheet.rowCount());
    mSpreadsheet.setCellContentAt(5, 5, "d");
    assertEquals(10, mSpreadsheet.rowCount());
    mSpreadsheet.setCellContentAt(1, 9, null);
    assertEquals(6, mSpreadsheet.rowCount());
  }
}
