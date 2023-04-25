package base;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public abstract class ModeTester extends ModelessTester {
    protected final int mode;

    protected ModeTester(final TestCounter counter, final int mode) {
        super(counter);
        this.mode = mode;
    }

    public int mode() {
        return mode;
    }
}
