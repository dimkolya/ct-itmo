package expression.generic.operators;

import expression.generic.modes.Mode;

public class Pow<T extends Number> extends AbstractBinaryOperator<T> {
    public Pow(final Mode<T> mode, final TripleExpression<T> base, TripleExpression<T> power) {
        super(mode, base, power);
    }

    @Override
    public String getOperation() {
        return "**";
    }

    @Override
    public int getPriority(boolean isRight) {
        if (isRight) {
            return 2048;
        } else {
            return 2047;
        }
    }

    @Override
    public T apply(T base, T power) {
        return mode.pow(base, power);
    }
}
