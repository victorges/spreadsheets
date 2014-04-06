package com.v1ct04.arithmetic;

/**
 * For parsing arithmetic expressions with variables, an instance of this interface is required for providing the values
 * of the variables, given their name. It's very possible that this method will try to parse another arithmetic
 * expression so it's been helpfully designed to return an {@link ArithmeticParseException} as well.
 */
public interface VariableValueProvider {

  public double getValueOf(String variableName) throws ArithmeticParseException;
}
