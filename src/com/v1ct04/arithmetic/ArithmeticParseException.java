package com.v1ct04.arithmetic;

import java.text.ParseException;

public class ArithmeticParseException extends ParseException {

  private final String mDisplayErrorCode;

  public ArithmeticParseException(String s, String displayErrorCode, int errorOffset) {
    super(s, errorOffset);
    mDisplayErrorCode = displayErrorCode;
  }

  public ArithmeticParseException(String s, String displayErrorCode) {
    this(s, displayErrorCode, -1);
  }

  public String getDisplayErrorCode() {
    return mDisplayErrorCode;
  }
}
