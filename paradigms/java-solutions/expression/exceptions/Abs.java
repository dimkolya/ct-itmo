package expression.exceptions;

import expression.TripleExpression;

public class Abs extends AbstractUnaryOperator {
    public Abs(TripleExpression expression) {
        super(expression);
    }

    @Override
    public String getOperation() {
        return "abs";
    }

    @Override
    public int apply(int value) {
        if (value == Integer.MIN_VALUE) {
            throw new OverflowException(String.format("Abs overflow: %s %d", getOperation(), value));
        }
        if (value < 0) {
            return -value;
        } else {
            return value;
        }
    }
}
