package expression.generic.operators;

import expression.generic.modes.Mode;

public class Divide<T extends Number> extends AbstractBinaryOperator<T> {
    public Divide(final Mode<T> mode, final TripleExpression<T> left, final TripleExpression<T> right) {
        super(mode, left, right);
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
    public T apply(T left, T right) {
        return mode.div(left, right);
    }
}