package expression.generic.operators;

import expression.generic.modes.Mode;

public class LZeroes<T extends Number> extends AbstractUnaryOperator<T> {
    public LZeroes(final Mode<T> mode, final TripleExpression<T> expression) {
        super(mode, expression);
    }

    @Override
    public String getOperation() {
        return "l0";
    }

    @Override
    public T apply(T value) {
        return mode.leadingZeroes(value);
    }
}