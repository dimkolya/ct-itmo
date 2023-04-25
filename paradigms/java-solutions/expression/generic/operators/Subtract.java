package expression.generic.operators;

import expression.generic.modes.Mode;

public class Subtract<T extends Number> extends AbstractBinaryOperator<T> {
    public Subtract(final Mode<T> mode, final TripleExpression<T> left, final TripleExpression<T> right) {
        super(mode, left, right);
    }

    @Override
    public String getOperation() {
        return "-";
    }

    @Override
    public int getPriority(boolean isRight) {
        if (isRight) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public T apply(T left, T right) {
        return mode.sub(left, right);
    }
}