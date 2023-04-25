package expression;

import java.math.BigInteger;
import java.math.BigDecimal;

public class Const implements GeneralExpression {
    private final Number value;

    public Const(final int value) {
        this.value = value;
    }

    public Const(final BigInteger value) {
        this.value = value;
    }

    public Const(final BigDecimal value) {
        this.value = value;
    }

    public int evaluate(final int x) {
        return value.intValue();
    }

    public int evaluate(final int x, final int y, final int z) {
        return value.intValue();
    }

    public BigInteger evaluate(final BigInteger x) {
        return (BigInteger)value;
    }

    public BigDecimal evaluate(final BigDecimal x) {
        return (BigDecimal)value;
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
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj != null && obj.getClass() == Const.class) {
            return value.equals(((Const) obj).value);
        }
        return false;
    }
}