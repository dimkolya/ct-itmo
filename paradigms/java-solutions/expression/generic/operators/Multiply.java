package expression.generic.operators;

import expression.generic.modes.Mode;

public class Multiply<T extends Number> extends AbstractBinaryOperator<T> {
    public Multiply(final Mode<T> mode, final TripleExpression<T> left, final TripleExpression<T> right) {
        super(mode, left, right);
    }

    @Override
    public String getOperation() {
        return "*";
    }

    @Override
    public int getPriority(boolean isRight) {
        return 1024;
    }

    @Override
    public T apply(T left, T right) {
        return mode.mul(left, right);
    }
}