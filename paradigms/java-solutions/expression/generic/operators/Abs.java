package expression.generic.operators;

import expression.generic.modes.Mode;

public class Abs<T extends Number> extends AbstractUnaryOperator<T> {
    public Abs(Mode<T> mode, TripleExpression<T> expression) {
        super(mode, expression);
    }

    @Override
    public String getOperation() {
        return "abs";
    }

    @Override
    public T apply(T value) {
        return mode.abs(value);
    }
}
