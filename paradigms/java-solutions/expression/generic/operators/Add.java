package expression.generic.operators;

import expression.generic.modes.Mode;

public class Add<T extends Number> extends AbstractBinaryOperator<T> {
    public Add(final Mode<T> mode, final TripleExpression<T> left, final TripleExpression<T> right) {
        super(mode, left, right);
    }

    @Override
    public String getOperation() {
        return "+";
    }

    @Override
    public int getPriority(boolean isRight) {
        return 0;
    }

    @Override
    public T apply(T left, T right) {
        return mode.add(left, right);
    }
}