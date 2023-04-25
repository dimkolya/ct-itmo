package expression;

import java.math.BigInteger;
import java.math.BigDecimal;

public class Negate extends AbstractUnaryOperator {
    public Negate(final GeneralExpression expression) {
        super(expression);
    }

    @Override
    public String getOperation() {
        return "-";
    }

    @Override
    public int apply(int value) {
        return -value;
    }
}