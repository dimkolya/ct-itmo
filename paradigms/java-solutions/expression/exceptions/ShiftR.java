package expression.exceptions;

import expression.TripleExpression;

public class ShiftR extends AbstractBinaryOperator {
    public ShiftR(final TripleExpression left, final TripleExpression right) {
        super(left, right);
    }

    @Override
    public String getOperation() {
        return ">>";
    }

    @Override
    public int getPriority(boolean isRight) {
        if (isRight) {
            return -1024;
        } else {
            return -1023;
        }
    }

    @Override
    public int apply(int left, int right) {
        return left >> right;
    }
}