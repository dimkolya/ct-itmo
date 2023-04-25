package expression;

import java.math.BigInteger;
import java.math.BigDecimal;

public class Variable implements GeneralExpression {
    private final String name;

    public Variable(final String name) {
        this.name = name;
    }

    @Override
    public int evaluate(int x) {
        return x;
    }

    @Override
    public int evaluate(int x, int y, int z) {
        switch (name) {
            case "x":
                return x;
            case "y":
                return y;
            case "z":
                return z;
            default:
                throw new AssertionError("Unknown name of variable: " + name);
        }
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public String toMiniString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj.getClass() == Variable.class) {
            return name.equals(((Variable) obj).name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}