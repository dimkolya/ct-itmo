package expression.generic.operators;

import expression.generic.modes.Mode;

public class Negate<T extends Number> extends AbstractUnaryOperator<T> {
    public Negate(final Mode<T> mode, final TripleExpression<T> expression) {
        super(mode, expression);
    }

    @Override
    public String getOperation() {
        return "-";
    }

    @Override
    public T apply(T value) {
        return mode.negate(value);
    }
}