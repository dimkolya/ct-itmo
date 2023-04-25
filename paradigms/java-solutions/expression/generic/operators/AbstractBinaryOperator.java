package expression.generic.operators;

import expression.generic.modes.Mode;

abstract class AbstractBinaryOperator<T extends Number> implements TripleExpression<T> {
    protected final Mode<T> mode;
    protected final TripleExpression<T> left;
    protected final TripleExpression<T> right;

    public AbstractBinaryOperator(final Mode<T> mode, final TripleExpression<T> left, final TripleExpression<T> right) {
        this.mode = mode;
        this.left = left;
        this.right = right;
    }

    protected abstract String getOperation();

    protected abstract int getPriority(boolean isRight);

    protected abstract T apply(T left, T right);

    @Override
    public T evaluate(T x, T y, T z) {
        return apply(left.evaluate(x, y, z), right.evaluate(x, y, z));
    }

    @Override
    public String toString() {
        return "(" + left.toString() + " " + getOperation() + " " + right.toString() + ")";
    }

    private StringBuilder putBrackets(TripleExpression<T> expression, boolean isRight) {
        StringBuilder sb = new StringBuilder();
        Integer thatPriority = null;
        if (expression instanceof AbstractBinaryOperator) {
            thatPriority = ((AbstractBinaryOperator<T>) expression).getPriority(isRight);
        }
        if (thatPriority == null || thatPriority >= getPriority(!isRight)) {
            sb.append(expression.toMiniString());
        } else {
            sb.append('(').append(expression.toMiniString()).append(')');
        }
        return sb;
    }

    @Override
    public String toMiniString() {
        return String.valueOf(putBrackets(left, false)) +
                ' ' + getOperation() + ' ' +
                putBrackets(right, true);
    }

    @Override
    public int hashCode() {
        return (left.hashCode() * 17 + right.hashCode()) * 17 + getClass().hashCode();
    }
}
