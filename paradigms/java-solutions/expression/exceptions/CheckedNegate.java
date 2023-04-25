package expression.exceptions;

import expression.TripleExpression;

public class CheckedNegate extends AbstractUnaryOperator {
    public CheckedNegate(final TripleExpression expression) {
        super(expression);
    }

    @Override
    public String getOperation() {
        return "-";
    }

    @Override
    public int apply(int value) {
        if (value == Integer.MIN_VALUE) {
            throw new OverflowException(String.format("CheckedNegate overflow: %s %d", getOperation(), value));
        }
        return -value;
    }
}