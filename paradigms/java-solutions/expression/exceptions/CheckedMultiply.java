package expression.exceptions;

import expression.TripleExpression;

public class CheckedMultiply extends AbstractBinaryOperator {
    public CheckedMultiply(final TripleExpression left, final TripleExpression right) {
        super(left, right);
    }

    public static boolean checkOverflow(int left, int right) {
        return (left > 0 && right > 0 && Integer.MAX_VALUE / left < right)
                || (left < 0 && right < 0 && Integer.MAX_VALUE / left > right)
                || (left > 0 && right < 0 && Integer.MIN_VALUE / left > right)
                || (left < 0 && right > 0 && Integer.MIN_VALUE / right > left);
    }

    @Override
    public String getOperation() {
        return "*";
    }

    @Override
    public int getPriority(boolean isRight) {
        return 1024;
    }

    @Override
    public int apply(int left, int right) {
        if (checkOverflow(left, right)) {
            throw new OverflowException(String.format("Multiply overflow: %d %s %d", left, getOperation(), right));
        }
        return left * right;
    }
}