package expression.generic.modes;

public interface Mode<T extends Number> {
    T valueOf(int value);

    T abs(T value);

    T negate(T value);

    T leadingZeroes(T value);

    T trailingZeroes(T value);

    T count(T value);

    T add(T left, T right);

    T div(T left, T right);

    T log(T left, T right);

    T mul(T left, T right);

    T pow(T left, T right);

    T sub(T left, T right);

    T shiftA(T left, T right);

    T shiftL(T left, T right);

    T shiftR(T left, T right);

    T min(T left, T right);

    T max(T left, T right);
}
