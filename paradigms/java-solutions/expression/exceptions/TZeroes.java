package expression.exceptions;

import expression.TripleExpression;

public class TZeroes extends AbstractUnaryOperator {
    public TZeroes(final TripleExpression expression) {
        super(expression);
    }

    @Override
    public String getOperation() {
        return "t0";
    }

    @Override
    public int apply(int value) {
        return Integer.numberOfTrailingZeros(value);
    }
}