package expression.generic.operators;

import expression.generic.modes.Mode;

public class TZeroes<T extends Number> extends AbstractUnaryOperator<T> {
    public TZeroes(final Mode<T> mode, final TripleExpression<T> expression) {
        super(mode, expression);
    }

    @Override
    public String getOperation() {
        return "l0";
    }

    @Override
    public T apply(T value) {
        return mode.trailingZeroes(value);
    }
}