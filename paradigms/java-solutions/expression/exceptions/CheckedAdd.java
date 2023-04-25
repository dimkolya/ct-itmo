package expression.exceptions;

import expression.TripleExpression;

public class CheckedAdd extends AbstractBinaryOperator {
    public CheckedAdd(final TripleExpression left, final TripleExpression right) {
        super(left, right);
    }

    @Override
    public String getOperation() {
        return "+";
    }

    @Override
    public int getPriority(boolean isRight) {
        return 0;
    }

    @Override
    public int apply(int left, int right) {
        if ((left >= 0 && right >= 0 && Integer.MAX_VALUE - right < left)
                || (left <= 0 && right <= 0 && Integer.MIN_VALUE - right > left)) {
            throw new OverflowException(String.format("Add overflow: %d %s %d", left, getOperation(), right));
        }
        return left + right;
    }
}