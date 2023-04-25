package expression.generic.modes;

public class IntegerMode implements Mode<Integer> {
    private final boolean checked;

    public IntegerMode(boolean checked) {
        this.checked = checked;
    }

    @Override
    public Integer valueOf(int value) {
        return value;
    }

    @Override
    public Integer abs(Integer value) {
        if (value == null) {
            return null;
        }
        if (checked && value == Integer.MIN_VALUE) {
            return null;
        }
        if (value < 0) {
            return -value;
        } else {
            return value;
        }
    }

    @Override
    public Integer negate(Integer value) {
        if (value == null) {
            return null;
        }
        if (checked && value == Integer.MIN_VALUE) {
            return null;
        }
        return -value;
    }

    public Integer leadingZeroes(Integer value) {
        if (value == null) {
            return null;
        }
        return Integer.numberOfLeadingZeros(value);
    }

    public Integer trailingZeroes(Integer value) {
        if (value == null) {
            return null;
        }
        return Integer.numberOfTrailingZeros(value);
    }

    @Override
    public Integer count(Integer value) {
        if (value == null) {
            return null;
        }
        return Integer.bitCount(value);
    }

    @Override
    public Integer add(Integer left, Integer right) {
        if (left == null || right == null) {
            return null;
        }
        if (checked && ((left >= 0 && right >= 0 && Integer.MAX_VALUE - right < left)
                    || (left <= 0 && right <= 0 && Integer.MIN_VALUE - right > left))) {
            return null;
        }
        return left + right;
    }

    @Override
    public Integer div(Integer left, Integer right) {
        if (left == null || right == null) {
            return null;
        }
        if (right == 0) {
            return null;
        } else if (checked && left == Integer.MIN_VALUE && right == -1) {
            return null;
        }
        return left / right;
    }

    public Integer log(Integer number, Integer base) {
        if (number == null || base == null) {
            return null;
        }
        if (checked && (number <= 0 || base <= 1)) {
            return null;
        }
        int ans = 0;
        while (number >= base) {
            number /= base;
            ans++;
        }
        return ans;
    }

    private static boolean checkMulOverflow(int left, int right) {
        return (left > 0 && right > 0 && Integer.MAX_VALUE / left < right)
                || (left < 0 && right < 0 && Integer.MAX_VALUE / left > right)
                || (left > 0 && right < 0 && Integer.MIN_VALUE / left > right)
                || (left < 0 && right > 0 && Integer.MIN_VALUE / right > left);
    }

    @Override
    public Integer mul(Integer left, Integer right) {
        if (left == null || right == null) {
            return null;
        }
        if (checked && checkMulOverflow(left, right)) {
            return null;
        }
        return left * right;
    }

    @Override
    public Integer pow(Integer power, Integer base) {
        if (power == null || base == null) {
            return null;
        }
        if (checked && (power < 0 || (base == 0 && power == 0))) {
            return null;
        }
        int ans = 1;
        while (power != 0) {
            if (power % 2 == 1) {
                if (checked && checkMulOverflow(ans, base)) {
                    return null;
                }
                ans *= base;
            }
            power /= 2;
            if (power != 0) {
                if (checked && checkMulOverflow(base, base)) {
                    return null;
                }
                base *= base;
            }
        }
        return ans;
    }

    @Override
    public Integer sub(Integer left, Integer right) {
        if (left == null || right == null) {
            return null;
        }
        if (checked && ((left >= 0 && right <= 0 && Integer.MAX_VALUE + right < left)
                    || (left <= 0 && right >= 0 && Integer.MIN_VALUE + right > left))) {
            return null;
        }
        return left - right;
    }

    public Integer shiftA(Integer left, Integer right) {
        if (left == null || right == null) {
            return null;
        }
        return left >>> right;
    }

    public Integer shiftL(Integer left, Integer right) {
        if (left == null || right == null) {
            return null;
        }
        return left << right;
    }

    public Integer shiftR(Integer left, Integer right) {
        if (left == null || right == null) {
            return null;
        }
        return left >> right;
    }

    @Override
    public Integer min(Integer left, Integer right) {
        if (left == null || right == null) {
            return null;
        }
        return Integer.min(left, right);
    }

    @Override
    public Integer max(Integer left, Integer right) {
        if (left == null || right == null) {
            return null;
        }
        return Integer.max(left, right);
    }
}
