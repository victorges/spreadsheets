package com.v1ct04.spreadsheets;


import com.v1ct04.arithmetic.ArithmeticExpressionParserTest;
import com.v1ct04.spreadsheets.model.SpreadsheetTest;
import com.v1ct04.spreadsheets.model.SpreadsheetUtilsTest;
import com.v1ct04.spreadsheets.ui.CellDisplayValueTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
        ArithmeticExpressionParserTest.class,
        SpreadsheetTest.class,
        SpreadsheetUtilsTest.class,
        CellDisplayValueTest.class
})
public class AllTests { }
