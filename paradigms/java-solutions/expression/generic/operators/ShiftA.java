package expression.generic.operators;

import expression.generic.modes.Mode;

public class ShiftA<T extends Number> extends AbstractBinaryOperator<T> {
    public ShiftA(final Mode<T> mode, final TripleExpression<T> left, final TripleExpression<T> right) {
        super(mode, left, right);
    }

    @Override
    public String getOperation() {
        return ">>>";
    }

    @Override
    public int getPriority(boolean isRight) {
        if (isRight) {
            return -1024;
        } else {
            return -1023;
        }
    }

    @Override
    public T apply(T left, T right) {
        return mode.shiftA(left, right);
    }
}