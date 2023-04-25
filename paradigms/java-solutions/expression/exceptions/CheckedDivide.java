package expression.exceptions;

import expression.TripleExpression;

public class CheckedDivide extends AbstractBinaryOperator {
    public CheckedDivide(final TripleExpression left, final TripleExpression right) {
        super(left, right);
    }

    @Override
    public String getOperation() {
        return "/";
    }

    @Override
    public int getPriority(boolean isRight) {
        if (isRight) {
            return 1023;
        } else {
            return 1025;
        }
    }

    @Override
    public int apply(int left, int right) {
        if (right == 0) {
            throw new DivisionByZeroException(String.format("Division by zero: %d %s %d", left, getOperation(), right));
        } else if (left == Integer.MIN_VALUE && right == -1) {
            throw new OverflowException(String.format("CheckedDivide overflow: %d %s %d", left, getOperation(), right));
        }
        return left / right;
    }
}