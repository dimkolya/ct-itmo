package expression.exceptions;

import expression.TripleExpression;

abstract class AbstractUnaryOperator implements TripleExpression {
    protected final TripleExpression expression;

    protected AbstractUnaryOperator(final TripleExpression expression) {
        this.expression = expression;
    }

    public abstract String getOperation();

    public abstract int apply(int value);

    @Override
    public int evaluate(int x, int y, int z) {
        return apply(expression.evaluate(x, y, z));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj.getClass() == getClass()) {
            return expression.equals(((AbstractUnaryOperator) obj).expression);
        }
        return false;
    }

    @Override
    public String toString() {
        return getOperation() + '(' + expression.toString() + ')';
    }

    @Override
    public String toMiniString() {
        if (expression instanceof AbstractBinaryOperator) {
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