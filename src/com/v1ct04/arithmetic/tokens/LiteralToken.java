package com.v1ct04.arithmetic.tokens;

public class LiteralToken extends OperandToken<Double> {

  private final Double mValue;

  public LiteralToken(Double value) {
    mValue = value;
  }

  @Override
  public Double getValue() {
    return mValue;
  }
}
