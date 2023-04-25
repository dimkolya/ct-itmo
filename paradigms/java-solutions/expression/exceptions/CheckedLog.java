package expression.exceptions;

import expression.TripleExpression;

public class CheckedLog extends AbstractBinaryOperator {
    public CheckedLog(TripleExpression number, TripleExpression base) {
        super(number, base);
    }

    @Override
    public String getOperation() {
        return "//";
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
    public int apply(int number, int base) {
        if (number <= 0 || base <= 1) {
            throw new LogIllegalArgumentException(String.format("Log illegal argument exception: %d %s %d",
                    number, getOperation(), base));
        }
        int ans = 0;
        while (number >= base) {
            number /= base;
            ans++;
        }
        return ans;
    }
}