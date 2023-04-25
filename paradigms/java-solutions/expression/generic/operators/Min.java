package expression.generic.operators;

import expression.generic.modes.Mode;

public class Min<T extends Number> extends AbstractBinaryOperator<T> {
    public Min(final Mode<T> mode, final TripleExpression<T> left, final TripleExpression<T> right) {
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
        return mode.min(left, right);
    }
}