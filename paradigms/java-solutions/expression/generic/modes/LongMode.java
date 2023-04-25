package expression.generic.modes;

public class LongMode implements Mode<Long> {
    @Override
    public Long valueOf(int value) {
        return (long) value;
    }

    @Override
    public Long abs(Long value) {
        if (value == null) {
            return null;
        }
        return Math.abs(value);
    }

    @Override
    public Long negate(Long value) {
        if (value == null) {
            return null;
        }
        return -value;
    }

    @Override
    public Long leadingZeroes(Long value) {
        if (value == null) {
            return null;
        }
        return (long) Long.numberOfLeadingZeros(value);
    }

    @Override
    public Long trailingZeroes(Long value) {
        if (value == null) {
            return null;
        }
        return (long) Long.numberOfTrailingZeros(value);
    }

    @Override
    public Long count(Long value) {
        if (value == null) {
            return null;
        }
        return (long) Long.bitCount(value);
    }

    @Override
    public Long add(Long left, Long right) {
        if (left == null || right == null) {
            return null;
        }
        return left + right;
    }

    @Override
    public Long div(Long left, Long right) {
        if (left == null || right == null || right == 0) {
            return null;
        }
        return left / right;
    }

    @Override
    public Long log(Long left, Long right) {
        if (left == null || right == null) {
            return null;
        }
        long ans = 0;
        while (left > 1) {
            left /= right;
            ans++;
        }
        return ans;
    }

    @Override
    public Long mul(Long left, Long right) {
        if (left == null || right == null) {
            return null;
        }
        return left * right;
    }

    @Override
    public Long pow(Long left, Long right) {
        if (left == null || right == null) {
            return null;
        }
        long ans = 1;
        while (right != 0) {
            if (right % 2 == 1) {
                ans *= left;
            }
            right /= 2;
            left *= left;
        }
        return ans;
    }

    @Override
    public Long sub(Long left, Long right) {
        if (left == null || right == null) {
            return null;
        }
        return left - right;
    }

    @Override
    public Long shiftA(Long left, Long right) {
        if (left == null || right == null) {
            return null;
        }
        return left >>> right;
    }

    @Override
    public Long shiftL(Long left, Long right) {
        if (left == null || right == null) {
            return null;
        }
        return left << right;
    }

    @Override
    public Long shiftR(Long left, Long right) {
        if (left == null || right == null) {
            return null;
        }
        return left >> right;
    }

    @Override
    public Long min(Long left, Long right) {
        if (left == null || right == null) {
            return null;
        }
        return Long.min(left, right);
    }

    @Override
    public Long max(Long left, Long right) {
        if (left == null || right == null) {
            return null;
        }
        return Long.max(left, right);
    }
}
