package expression.generic;

import java.util.Map;
import expression.generic.modes.*;
import expression.generic.parser.*;
import expression.generic.operators.*;

public class GenericTabulator implements Tabulator {
    private static final Map<String, Mode<? extends Number>> MODES = Map.of(
            "i", new IntegerMode(true),
            "d", new DoubleMode(),
            "bi", new BigIntegerMode(),
            "u", new IntegerMode(false),
            "l", new LongMode(),
            "t", new TenMode()
    );

    public static void main(String[] args) {
        if (args.length != 2
                || args[0] == null || args[1] == null
                || args[0].charAt(0) != '-' || args[0].length() < 2) {
            throw new IllegalArgumentException("Please, enter 2 arguments: evaluate mode and expression.\n");
        }

        final Object[][][] result = new GenericTabulator().tabulate(args[0], args[1],
                -2, 2,
                -2, 2,
                -2, 2);

        for (int x = 0; x <= 4; x++) {
            for (int y = 0; y <= 4; y++) {
                for (int z = 0; z <= 4; z++) {
                    System.out.printf("[%d, %d, %d]: %s", x - 2, y - 2, z - 2, result[x][y][z]);
                }
            }
        }
    }

    private <T extends Number> Object[][][] tabulate(final Mode<T> mode, final String expressionString,
                                                     final int x1, final int x2,
                                                     final int y1, final int y2,
                                                     final int z1, final int z2) {
        final TripleExpression<T> expression = new ExpressionParser<T>(mode).parse(expressionString);
        Object[][][] result = new Object[x2 - x1 + 1][y2 - y1 + 1][z2 - z1 + 1];
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                for (int z = z1; z <= z2; z++) {
                    result[x - x1][y - y1][z - z1] = expression.evaluate(
                            mode.valueOf(x),
                            mode.valueOf(y),
                            mode.valueOf(z)
                    );
                }
            }
        }
        return result;
    }

    @Override
    public Object[][][] tabulate(final String modeString, final String expression,
                                     final int x1, final int x2,
                                     final int y1, final int y2,
                                     final int z1, final int z2) throws IllegalArgumentException {
        final Mode<? extends Number> mode = MODES.get(modeString);
        if (mode == null) {
            throw new IllegalArgumentException("Unknown mode: " + modeString);
        }
        return tabulate(mode, expression, x1, x2, y1, y2, z1, z2);
    }
}
