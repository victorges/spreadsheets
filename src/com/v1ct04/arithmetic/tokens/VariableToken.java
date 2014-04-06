package com.v1ct04.arithmetic.tokens;

public class VariableToken extends OperandToken<String> {

  private final String mVariableName;

  public VariableToken(String variableName) {
    mVariableName = variableName;
  }

  @Override
  public String getValue() {
    return mVariableName;
  }
}
