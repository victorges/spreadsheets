package com.v1ct04.spreadsheets.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class SpreadsheetUtilsTest {

  @Test
  public void testColumnNameParsing() {
    assertEquals(0, SpreadsheetUtils.columnIndexForName("A"));
    assertEquals(10, SpreadsheetUtils.columnIndexForName("K"));
    assertEquals(54, SpreadsheetUtils.columnIndexForName("BC"));
    assertEquals(129, SpreadsheetUtils.columnIndexForName("DZ"));
  }

  @Test
  public void testCellNameParsing() {
    assertEquals(new CellLocation(0, 0), SpreadsheetUtils.cellLocationForName("A1"));
    assertEquals(new CellLocation(6, 4), SpreadsheetUtils.cellLocationForName("G5"));
    assertEquals(new CellLocation(26, 44), SpreadsheetUtils.cellLocationForName("AA45"));
    assertEquals(new CellLocation(84, 124), SpreadsheetUtils.cellLocationForName("CG125"));
  }

  @Test
  public void testColumnNameConsistency() {
    String[] names = {"ABA", "SFD", "ASAT", "LEET", "FGDHT", "PLNTR", "VICTOR"};
    for (String name : names) {
      int columnIndex = SpreadsheetUtils.columnIndexForName(name);
      assertEquals(name, SpreadsheetUtils.columnNameForIndex(columnIndex));
    }
  }

  @Test
  public void testCellNameConsistency() {
    String[] names = {"A11", "SDF34", "GFH546", "AAA111", "QWERT1234", "VICTOR1902"};
    for (String name : names) {
      CellLocation location = SpreadsheetUtils.cellLocationForName(name);
      assertEquals(name, SpreadsheetUtils.cellNameForLocation(location));
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidColumnName() {
    SpreadsheetUtils.columnIndexForName("a");
  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvalidCellName() {
    SpreadsheetUtils.cellLocationForName("PLNTR234V");
  }
}
