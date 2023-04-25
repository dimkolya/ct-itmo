package expression.generic.operators;

import expression.generic.modes.Mode;

abstract class AbstractUnaryOperator<T extends Number> implements TripleExpression<T> {
    protected final Mode<T> mode;
    protected final TripleExpression<T> expression;

    public AbstractUnaryOperator(final Mode<T> mode, final TripleExpression<T> expression) {
        this.mode = mode;
        this.expression = expression;
    }

    protected abstract String getOperation();

    protected abstract T apply(T expression);

    @Override
    public T evaluate(T x, T y, T z) {
        return apply(expression.evaluate(x, y, z));
    }

    @Override
    public String toString() {
        return getOperation() + '(' + expression.toString() + ')';
    }

    @Override
    public String toMiniString() {
        if (expression instanceof AbstractBinaryOperator<T>) {
            return getOperation() + '(' + expression.toMiniString() + ')';
        } else {
            return getOperation() + ' ' + expression.toMiniString();
        }
    }

    @Override
    public int hashCode() {
        return expression.hashCode() * 17 + getClass().hashCode();
    }
}
