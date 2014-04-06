package com.v1ct04.arithmetic;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class ArithmeticExpressionParserTest {

  private static final double EPS = 1e-6;

  private ArithmeticExpressionParser mExpressionParser;
  private VariableValueMap mVariableValueProvider;

  @Before
  public void setUp() {
    mVariableValueProvider = new VariableValueMap();
    mExpressionParser = new ArithmeticExpressionParser();
    mExpressionParser.setVariableValueProvider(mVariableValueProvider);
  }

  @Test
  public void testExpressionEvaluation() throws ArithmeticParseException {
    assertThatExpressionEvaluatesTo(10, "1+2+3+4");
    assertThatExpressionEvaluatesTo(2, "-3 + 7 - 2");
    assertThatExpressionEvaluatesTo(-9.5, "-5+3-3/2*5");
    assertThatExpressionEvaluatesTo(-8.8, "-1 * 2 * ((3)+4)/2 - 3/(5+5) * 6");
    assertThatExpressionEvaluatesTo(-98.821148, "-7.3 * (3.583) * (3*(4.342-1.12))/2.4 - 3/(1.54-2)");
  }

  @Test
  public void testPathologicalExpressionsEvaluation() throws ArithmeticParseException {
    assertThatExpressionEvaluatesTo(Double.POSITIVE_INFINITY, "1/(1-1)"); // Positive / 0
    assertThatExpressionEvaluatesTo(Double.NEGATIVE_INFINITY, "-2/(1+(1-2))"); // Negative / 0
    assertThatExpressionEvaluatesTo(0, "1/(1/0)");
    assertThatExpressionEvaluatesTo(Double.NaN, "(5-4-1)/(1-1)"); // 0/0
  }

  @Test
  public void testExpressionEvaluationWithVariables1() throws ArithmeticParseException {
    mVariableValueProvider.setValueOf("A1", 2.0);
    mVariableValueProvider.setValueOf("B7", 3.0);
    assertThatExpressionEvaluatesTo(73, "2 * A1 + (20 + B7) * 3");
  }

  @Test
  public void testExpressionEvaluationWithVariables2() throws ArithmeticParseException {
    mVariableValueProvider.setValueOf("x", 1.0);
    mVariableValueProvider.setValueOf("y", 3.0);
    assertThatExpressionEvaluatesTo(13, "x + y * (x + y)");
  }

  @Test(expected = ArithmeticParseException.class)
  public void testMalformedExpression() throws ArithmeticParseException {
    assertParseExceptionFor("*1 2");
  }

  @Test(expected = ArithmeticParseException.class)
  public void testUnbalancedParenthesis() throws ArithmeticParseException {
    assertParseExceptionFor("(3+4)-3+(5+(3+(1*(10)))");
  }

  @Test(expected = ArithmeticParseException.class)
  public void testInvalidOperatorSequence() throws ArithmeticParseException {
    assertParseExceptionFor("4+4*10-5+*3-2");
  }

  private void assertThatExpressionEvaluatesTo(double expected, String expression) throws ArithmeticParseException {
    double result = mExpressionParser.evaluate(expression);
    assertEquals(expression + " didn't evaluate to expected value", expected, result, EPS);
  }

  private void assertParseExceptionFor(String expression) throws ArithmeticParseException {
    mExpressionParser.evaluate(expression);
  }

  private class VariableValueMap implements VariableValueProvider {

    private Map<String, Double> mVariables = new HashMap<>();


    public void setValueOf(String variable, Double value) {
      mVariables.put(variable, value);
    }

    @Override
    public double getValueOf(String variableName) throws ArithmeticParseException {
      return mVariables.get(variableName);
    }
  }
}
