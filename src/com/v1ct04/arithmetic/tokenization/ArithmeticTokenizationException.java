package com.v1ct04.arithmetic.tokenization;

import com.v1ct04.arithmetic.ArithmeticParseException;

public class ArithmeticTokenizationException extends ArithmeticParseException {

  public ArithmeticTokenizationException(String s, String displayErrorCode, int errorOffset) {
    super(s, displayErrorCode, errorOffset);
  }
}
