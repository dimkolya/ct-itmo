package expression.generic.modes;

import java.math.BigInteger;

public class DoubleMode implements Mode<Double> {
    @Override
    public Double valueOf(int value) {
        return (double) value;
    }

    @Override
    public Double abs(Double value) {
        return Math.abs(value);
    }

    @Override
    public Double negate(Double value) {
        return -value;
    }

    @Override
    public Double leadingZeroes(Double value) {
        return null;
    }

    @Override
    public Double trailingZeroes(Double value) {
        return null;
    }

    @Override
    public Double count(Double value) {
        return (double) Long.bitCount(Double.doubleToLongBits(value));
    }

    @Override
    public Double add(Double left, Double right) {
        return left + right;
    }

    @Override
    public Double div(Double left, Double right) {
        return left / right;
    }

    @Override
    public Double log(Double left, Double right) {
        return null;
    }

    @Override
    public Double mul(Double left, Double right) {
        return left * right;
    }

    @Override
    public Double pow(Double left, Double right) {
        return null;
    }

    @Override
    public Double sub(Double left, Double right) {
        return left - right;
    }

    @Override
    public Double shiftA(Double left, Double right) {
        return null;
    }

    @Override
    public Double shiftL(Double left, Double right) {
        return null;
    }

    @Override
    public Double shiftR(Double left, Double right) {
        return null;
    }

    @Override
    public Double min(Double left, Double right) {
        if (left == null || right == null) {
            return null;
        }
        return Double.min(left, right);
    }

    @Override
    public Double max(Double left, Double right) {
        if (left == null || right == null) {
            return null;
        }
        return Double.max(left, right);
    }
}
