package base;

import java.util.Locale;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public abstract class ModelessTester {
    protected final TestCounter counter;

    public ModelessTester(final TestCounter counter) {
        this.counter = counter;

        Locale.setDefault(Locale.US);
        Asserts.checkAssert(getClass());
    }

    public abstract void test();

    public ExtendedRandom random() {
        return counter.random();
    }
}
