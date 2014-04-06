package com.v1ct04.arithmetic.tokens;

import com.v1ct04.arithmetic.tokens.ParenthesisToken.ParenthesisType;

public abstract class OperandToken<Type> implements Token<Type> {

  @Override
  public boolean isValidAfter(Token<?> previousToken) {
    return (previousToken == null ||
            previousToken instanceof OperatorToken ||
            previousToken.getValue() == ParenthesisType.OPEN);
  }
}
