package expression.generic.operators;

import expression.generic.modes.Mode;

public class Count<T extends Number> extends AbstractUnaryOperator<T> {
    public Count(Mode<T> mode, TripleExpression<T> expression) {
        super(mode, expression);
    }

    @Override
    public String getOperation() {
        return "count";
    }

    @Override
    public T apply(T value) {
        return mode.count(value);
    }
}
