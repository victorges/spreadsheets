package com.v1ct04.arithmetic.tokens;

import com.v1ct04.arithmetic.tokens.ParenthesisToken.ParenthesisType;

public class OperatorToken implements Token<OperatorToken.Operation>, Comparable<OperatorToken> {

  public enum Operation {
    PLUS, MINUS, TIMES, DIVISION
  }

  public static boolean isOperator(char c) {
    return c == '+' || c == '-' || c == '*' || c == '/';
  }

  private Operation mOperation;

  public OperatorToken(Character character) {
    if (character.equals('+')) mOperation = Operation.PLUS;
    else if (character.equals('-')) mOperation = Operation.MINUS;
    else if (character.equals('*')) mOperation = Operation.TIMES;
    else if (character.equals('/')) mOperation = Operation.DIVISION;
    else throw new IllegalArgumentException("Invalid character for binary operator: " + character);
  }

  public Double operate(Double op1, Double op2) {
    switch (mOperation) {
      case PLUS: return op1 + op2;
      case MINUS: return op1 - op2;
      case TIMES: return op1 * op2;
      case DIVISION: return op1 / op2;
    }
    throw new IllegalStateException();
  }

  public int getPriority() {
    switch (mOperation) {
      case TIMES: case DIVISION:
        return 2;
      case PLUS: case MINUS:
        return 1;
    }
    throw new IllegalStateException();
  }

  @Override
  public Operation getValue() {
    return mOperation;
  }

  @Override
  public boolean isValidAfter(Token<?> previousToken) {
    if (previousToken == null || previousToken.getValue() == ParenthesisType.OPEN) {
      return mOperation == Operation.PLUS || mOperation == Operation.MINUS;
    }
    return (previousToken instanceof OperandToken ||
            previousToken.getValue() == ParenthesisType.CLOSE);
  }

  @Override
  public int compareTo(OperatorToken o) {
    return getPriority() - o.getPriority();
  }
}
