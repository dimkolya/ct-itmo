package expression.exceptions;

import expression.TripleExpression;

public class LZeroes extends AbstractUnaryOperator {
    public LZeroes(final TripleExpression expression) {
        super(expression);
    }

    @Override
    public String getOperation() {
        return "l0";
    }

    @Override
    public int apply(int value) {
        return Integer.numberOfLeadingZeros(value);
    }
}