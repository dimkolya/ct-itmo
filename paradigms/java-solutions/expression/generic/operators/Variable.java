package expression.generic.operators;

public class Variable<T extends Number> implements TripleExpression<T> {
    private final String name;

    public Variable(final String name) {
        this.name = name;
    }

    @Override
    public T evaluate(T x, T y, T z) {
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
    public int hashCode() {
        return name.hashCode();
    }
}