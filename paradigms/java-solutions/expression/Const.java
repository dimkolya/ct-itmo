package expression;

import java.math.BigInteger;
import java.math.BigDecimal;

public class Const implements GeneralExpression {
    private final Number value;

    public Const(final int value) {
        this.value = value;
    }

    public int evaluate(final int x) {
        return value.intValue();
    }

    @Override
    public int evaluate(final int x, final int y, final int z) {
        return value.intValue();
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public String toMiniString() {
        return value.toString();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj != null && obj.getClass() == Const.class) {
            return value.equals(((Const) obj).value);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}