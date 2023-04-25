package expression.generic.operators;

import expression.generic.modes.Mode;

public class Max<T extends Number> extends AbstractBinaryOperator<T> {
    public Max(final Mode<T> mode, final TripleExpression<T> left, final TripleExpression<T> right) {
        super(mode, left, right);
    }

    @Override
    public String getOperation() {
        return "min";
    }

    @Override
    public int getPriority(boolean isRight) {
        return -2048;
    }

    @Override
    public T apply(T left, T right) {
        return mode.max(left, right);
    }
}