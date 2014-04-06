package com.v1ct04.arithmetic.tokens;

public class ParenthesisToken implements Token<ParenthesisToken.ParenthesisType> {

  public enum ParenthesisType {
    OPEN, CLOSE
  }

  public static boolean isParenthesis(char c) {
    return c == '(' || c == ')';
  }

  private ParenthesisType mType;

  public ParenthesisToken(Character character) {
    if (character.equals('(')) mType = ParenthesisType.OPEN;
    else if (character.equals(')')) mType = ParenthesisType.CLOSE;
    else throw new IllegalArgumentException("Invalid character for parenthesis token: " + character);
  }

  @Override
  public ParenthesisType getValue() {
    return mType;
  }

  @Override
  public boolean isValidAfter(Token<?> previousToken) {
    switch (mType) {
      case CLOSE:
        return (previousToken instanceof OperandToken ||
                previousToken.getValue() == ParenthesisType.CLOSE);
      case OPEN:
        return (previousToken == null ||
                previousToken instanceof OperatorToken ||
                previousToken.getValue() == ParenthesisType.OPEN);
    }
    throw new IllegalStateException();
  }
}
