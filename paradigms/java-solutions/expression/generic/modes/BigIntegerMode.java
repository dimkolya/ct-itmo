package expression.generic.modes;

import java.math.BigInteger;

public class BigIntegerMode implements Mode<BigInteger> {
    @Override
    public BigInteger valueOf(int value) {
        return BigInteger.valueOf(value);
    }

    @Override
    public BigInteger abs(BigInteger value) {
        if (value == null) {
            return null;
        }
        return value.abs();
    }

    @Override
    public BigInteger negate(BigInteger value) {
        if (value == null) {
            return null;
        }
        return value.negate();
    }

    @Override
    public BigInteger leadingZeroes(BigInteger value) {
        return null;
    }

    @Override
    public BigInteger trailingZeroes(BigInteger value) {
        return null;
    }

    @Override
    public BigInteger count(BigInteger value) {
        if (value == null) {
            return null;
        }
        return BigInteger.valueOf(value.bitCount());
    }

    @Override
    public BigInteger add(BigInteger left, BigInteger right) {
        if (left == null || right == null) {
            return null;
        }
        return left.add(right);
    }

    @Override
    public BigInteger div(BigInteger left, BigInteger right) {
        if (left == null || right == null) {
            return null;
        }
        if (right.equals(BigInteger.ZERO)) {
            return null;
        }
        return left.divide(right);
    }

    @Override
    public BigInteger log(BigInteger left, BigInteger right) {
        return null;
    }

    @Override
    public BigInteger mul(BigInteger left, BigInteger right) {
        if (left == null || right == null) {
            return null;
        }
        return left.multiply(right);
    }

    @Override
    public BigInteger pow(BigInteger left, BigInteger right) {
        return null;
    }

    @Override
    public BigInteger sub(BigInteger left, BigInteger right) {
        if (left == null || right == null) {
            return null;
        }
        return left.subtract(right);
    }

    @Override
    public BigInteger shiftA(BigInteger left, BigInteger right) {
        return null;
    }

    @Override
    public BigInteger shiftL(BigInteger left, BigInteger right) {
        return null;
    }

    @Override
    public BigInteger shiftR(BigInteger left, BigInteger right) {
        return null;
    }

    @Override
    public BigInteger min(BigInteger left, BigInteger right) {
        if (left == null || right == null) {
            return null;
        }
        return left.min(right);
    }

    @Override
    public BigInteger max(BigInteger left, BigInteger right) {
        if (left == null || right == null) {
            return null;
        }
        return left.max(right);
    }
}
