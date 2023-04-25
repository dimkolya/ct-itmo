package expression.exceptions;

import expression.TripleExpression;

public class CheckedPow extends AbstractBinaryOperator {
    public CheckedPow(TripleExpression base, TripleExpression power) {
        super(base, power);
    }

    @Override
    public String getOperation() {
        return "**";
    }

    @Override
    public int getPriority(boolean isRight) {
        if (isRight) {
            return 2048;
        } else {
            return 2049;
        }
    }

    @Override
    public int apply(int base, int power) {
        if (power < 0 || (base == 0 && power == 0)) {
            throw new NegativePowerException(String.format("Negative power: %d %s %d", base, getOperation(), power));
        }
        int ans = 1;
        while (power != 0) {
            if (power % 2 == 1) {
                if (CheckedMultiply.checkOverflow(ans, base)) {
                    throw new OverflowException(String.format("Pow overflow: %d %s %d", base, getOperation(), power));
                }
                ans *= base;
            }
            power /= 2;
            if (power != 0) {
                if (CheckedMultiply.checkOverflow(base, base)) {
                    throw new OverflowException(String.format("Pow overflow: %d %s %d", base, getOperation(), power));
                }
                base *= base;
            }
        }
        return ans;
    }
}
