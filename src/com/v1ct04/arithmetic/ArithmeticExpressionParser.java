package com.v1ct04.arithmetic;

import com.v1ct04.arithmetic.tokenization.ArithmeticExpressionTokenizer;
import com.v1ct04.arithmetic.tokens.*;
import com.v1ct04.arithmetic.tokens.ParenthesisToken.ParenthesisType;

import java.util.Deque;
import java.util.LinkedList;

public class ArithmeticExpressionParser {

  private VariableValueProvider mVariableValueProvider;

  public void setVariableValueProvider(VariableValueProvider variableValueProvider) {
    mVariableValueProvider = variableValueProvider;
  }

  public double evaluate(String expression) throws ArithmeticParseException {
    ArithmeticExpressionTokenizer tokenizer = new ArithmeticExpressionTokenizer(expression);
    return processExpression(tokenizer);
  }

  private Double processExpression(ArithmeticExpressionTokenizer tokenizer) throws ArithmeticParseException {
    Deque<OperandToken> operandStack = new LinkedList<>();
    Deque<OperatorToken> operatorStack = new LinkedList<>();
    while (tokenizer.hasNext()) {
      Token<?> token = tokenizer.nextToken();
      if (token instanceof OperandToken) {
        operandStack.offerLast((OperandToken) token);
      } else if (token instanceof OperatorToken) {
        processOperatorToken((OperatorToken) token, operandStack, operatorStack);
      } else if (token instanceof ParenthesisToken) {
        ParenthesisType type = ((ParenthesisToken) token).getValue();
        if (type == ParenthesisType.OPEN) {
          Double parenthesisValue = processExpression(tokenizer);
          operandStack.offerLast(new LiteralToken(parenthesisValue));
        } else if (type == ParenthesisType.CLOSE) {
          break;
        }
      } else {
        throw new IllegalStateException("Unrecognized token class");
      }
    }
    while (!operatorStack.isEmpty()) {
      executeStackedOperation(operandStack, operatorStack);
    }
    return pollLastOperandDoubleValue(operandStack);
  }

  private void processOperatorToken(OperatorToken currentOperator,
                                    Deque<OperandToken> operandStack,
                                    Deque<OperatorToken> operatorStack)
          throws ArithmeticParseException {
    // execute stacked operations while the current operator priority is lower than or equal to the last one stacked
    while (!operatorStack.isEmpty() && currentOperator.compareTo(operatorStack.peekLast()) <= 0) {
      executeStackedOperation(operandStack, operatorStack);
    }
    // in case the string starts with an operator (necessarily + or -), pretend we have "0+..." or "0-..."
    if (operandStack.isEmpty()) {
      operandStack.offerLast(new LiteralToken(0.0));
    }
    operatorStack.offerLast(currentOperator);
  }

  private void executeStackedOperation(Deque<OperandToken> operandStack, Deque<OperatorToken> operatorStack)
          throws ArithmeticParseException {
    Double op2 = pollLastOperandDoubleValue(operandStack);
    Double op1 = pollLastOperandDoubleValue(operandStack);
    Double result = operatorStack.pollLast().operate(op1, op2);
    operandStack.offerLast(new LiteralToken(result));
  }

  private Double pollLastOperandDoubleValue(Deque<OperandToken> operandStack) throws ArithmeticParseException {
    OperandToken last = operandStack.pollLast();
    if (last instanceof LiteralToken) {
      return ((LiteralToken)last).getValue();
    } else if (last instanceof VariableToken) {
      return mVariableValueProvider.getValueOf(((VariableToken)last).getValue());
    }
    return Double.NaN;
  }
}
