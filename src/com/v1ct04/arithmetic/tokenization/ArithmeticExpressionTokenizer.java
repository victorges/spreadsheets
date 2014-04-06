package com.v1ct04.arithmetic.tokenization;

import com.v1ct04.arithmetic.tokens.*;
import com.v1ct04.arithmetic.tokens.ParenthesisToken.ParenthesisType;

import java.util.NoSuchElementException;

public class ArithmeticExpressionTokenizer {

  private final String mExpression;

  private int mCurrentIndex = 0;
  private Token mLastToken = null;
  private int mParenthesesCount = 0;

  public ArithmeticExpressionTokenizer(String expression) {
    mExpression = expression;
    // initialize the current index with the first non-whitespace character
    skipWhitespace();
  }

  public boolean hasNext() {
    return mCurrentIndex < mExpression.length();
  }

  public Token nextToken() throws ArithmeticTokenizationException {
    if (!hasNext()) throw new NoSuchElementException("Next element does not exist");

    char c = mExpression.charAt(mCurrentIndex);
    Token<?> token;
    if (isDigit(c)) {
      token = readNumber();
    } else if (isLetter(c)) {
      token = readVariable();
    } else if (OperatorToken.isOperator(c)) {
      mCurrentIndex++;
      token = new OperatorToken(c);
    } else if (ParenthesisToken.isParenthesis(c)) {
      mCurrentIndex++;
      token = new ParenthesisToken(c);
      mParenthesesCount += (token.getValue() == ParenthesisType.OPEN ? 1 : -1);
      assertThat(mParenthesesCount >= 0, "Unbalanced parenthesis", "#!UNBLN", mCurrentIndex);
    } else {
      throw new ArithmeticTokenizationException("Unrecognized character: " + c, "#!INVLD", mCurrentIndex);
    }
    assertThat(token.isValidAfter(mLastToken), "Malformed expression", "#!INVLD", mCurrentIndex);
    mLastToken = token;

    skipWhitespace();
    if (!hasNext()) {
      assertThat(mParenthesesCount == 0, "Unbalanced parenthesis", "#!UNBLN", mCurrentIndex);
    }
    return token;
  }

  private Token<Double> readNumber() {
    int startIndex = mCurrentIndex++;
    while (mCurrentIndex < mExpression.length()) {
      char c = mExpression.charAt(mCurrentIndex);
      if (isDigit(c) || c == '.') {
        mCurrentIndex++;
      } else {
        break;
      }
    }
    Double value = Double.valueOf(mExpression.substring(startIndex, mCurrentIndex));
    return new LiteralToken(value);
  }

  private Token<String> readVariable() {
    int startIndex = mCurrentIndex++;
    while (mCurrentIndex < mExpression.length()) {
      char c = mExpression.charAt(mCurrentIndex);
      if (isLetter(c) || isDigit(c)) {
        mCurrentIndex++;
      } else {
        break;
      }
    }
    String name = mExpression.substring(startIndex, mCurrentIndex);
    return new VariableToken(name);
  }

  private void skipWhitespace() {
    while (mCurrentIndex < mExpression.length()) {
      char c = mExpression.charAt(mCurrentIndex);
      if (Character.isWhitespace(c)) mCurrentIndex++;
      else break;
    }
  }

  private static boolean isLetter(char c) {
    return (c >= 'A' && c <= 'z');
  }

  private static boolean isDigit(char c) {
    return (c >= '0' && c <= '9');
  }

  private static void assertThat(boolean assertion, String message, String displayValue, int location)
          throws ArithmeticTokenizationException {
    if (!assertion) {
      throw new ArithmeticTokenizationException(message, displayValue, location);
    }
  }
}
