package expression;

abstract class AbstractBinaryOperator implements GeneralExpression {
    protected final GeneralExpression left;
    protected final GeneralExpression right;

    protected AbstractBinaryOperator(final GeneralExpression left, final GeneralExpression right) {
        this.left = left;
        this.right = right;
    }

    public abstract String getOperation();

    public abstract int getPriority(boolean isRight);

    public abstract int apply(int left, int right);

    @Override
    public int evaluate(int x) {
        return apply(left.evaluate(x), right.evaluate(x));
    }

    @Override
    public int evaluate(int x, int y, int z) {
        return apply(left.evaluate(x, y, z), right.evaluate(x, y, z));
    }

    @Override
    public String toString() {
        return "(" + left.toString() + " " + getOperation() + " " + right.toString() + ")";
    }

    private StringBuilder putBrackets(GeneralExpression expression, boolean isRight) {
        StringBuilder sb = new StringBuilder();
        Integer thatPriority = null;
        if (expression instanceof AbstractBinaryOperator) {
            thatPriority = ((AbstractBinaryOperator) expression).getPriority(isRight);
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
        StringBuilder sb = new StringBuilder();
        sb.append(putBrackets(left, false));
        sb.append(' ').append(getOperation()).append(' ');
        sb.append(putBrackets(right, true));
        return sb.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj != null && obj.getClass() == getClass()) {
            final AbstractBinaryOperator that = (AbstractBinaryOperator) obj;
            return left.equals(that.left) && right.equals(that.right);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (left.hashCode() * 17 + right.hashCode()) * 17 + getClass().hashCode();
    }
}
