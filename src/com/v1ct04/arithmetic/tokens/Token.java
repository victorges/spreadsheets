package com.v1ct04.arithmetic.tokens;

/**
 * Class that represents a token in an arithmetic exception that can be processed. Contains methods for validating the
 * expression during tokenization ({@link Token#isValidAfter(Token)}) and for helping processing the tokens when
 * actually evaluating the expression. Implementations may introduce even more helpers for those operations.
 */
public interface Token<Type> {

  Type getValue();

  boolean isValidAfter(Token<?> previousToken);
}
