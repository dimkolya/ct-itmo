package expression.generic.modes;

public class TenMode implements Mode<Integer> {
    private Integer tenMode(Integer result) {
        if (result == null) {
            return null;
        }
        return result - result % 10;
    }
    
    @Override
    public Integer valueOf(int value) {
        return tenMode(value);
    }

    @Override
    public Integer abs(Integer value) {
        if (value == null) {
            return null;
        }
        if (value < 0) {
            value = -value;
        }
        return tenMode(value);
    }

    @Override
    public Integer negate(Integer value) {
        if (value == null) {
            return null;
        }
        return tenMode(-value);
    }

    @Override
    public Integer leadingZeroes(Integer value) {
        if (value == null) {
            return null;
        }
        return tenMode(Integer.numberOfLeadingZeros(value));
    }

    @Override
    public Integer trailingZeroes(Integer value) {
        if (value == null) {
            return null;
        }
        return tenMode(Integer.numberOfTrailingZeros(value));
    }

    @Override
    public Integer count(Integer value) {
        if (value == null) {
            return null;
        }
        return tenMode(Integer.bitCount(value));
    }

    @Override
    public Integer add(Integer left, Integer right) {
        if (left == null || right == null) {
            return null;
        }
        return tenMode(left + right);
    }

    @Override
    public Integer div(Integer left, Integer right) {
        if (left == null || right == null || right == 0) {
            return null;
        }
        return tenMode(left / right);
    }

    @Override
    public Integer log(Integer left, Integer right) {
        return null;
    }

    @Override
    public Integer mul(Integer left, Integer right) {
        if (left == null || right == null) {
            return null;
        }
        return tenMode(left * right);
    }

    @Override
    public Integer pow(Integer left, Integer right) {
        return null;
    }

    @Override
    public Integer sub(Integer left, Integer right) {
        if (left == null || right == null) {
            return null;
        }
        return tenMode(left - right);
    }

    @Override
    public Integer shiftA(Integer left, Integer right) {
        if (left == null || right == null) {
            return null;
        }
        return tenMode(left >>> right);
    }

    @Override
    public Integer shiftL(Integer left, Integer right) {
        if (left == null || right == null) {
            return null;
        }
        return tenMode(left << right);
    }

    @Override
    public Integer shiftR(Integer left, Integer right) {
        if (left == null || right == null) {
            return null;
        }
        return tenMode(left >> right);
    }

    @Override
    public Integer min(Integer left, Integer right) {
        if (left == null || right == null) {
            return null;
        }
        return tenMode(Integer.min(left, right));
    }

    @Override
    public Integer max(Integer left, Integer right) {
        if (left == null || right == null) {
            return null;
        }
        return tenMode(Integer.max(left, right));
    }
}
