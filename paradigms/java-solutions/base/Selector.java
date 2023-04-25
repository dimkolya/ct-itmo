package base;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * @author Georgiy Korneev (kgeorgiy@kgeorgiy.info)
 */
public final class Selector<V> {
    private final Class<?> owner;
    private final BiConsumer<V, TestCounter> test;

    private final Map<String, List<V>> variants = new LinkedHashMap<>();

    private Selector(final Class<?> owner, final BiConsumer<V, TestCounter> test) {
        this.owner = owner;
        this.test = test;
    }

    public static <V> Selector<V> create(final Class<?> owner, final BiConsumer<V, TestCounter> test) {
        return new Selector<>(owner, test);
    }

    public static Selector<Consumer<TestCounter>> create(final Class<?> owner) {
        return new Selector<>(owner, Consumer::accept);
    }

    @SafeVarargs
    public final Selector<V> variant(final String name, final V... operations) {
        Asserts.assertTrue("Duplicate variant " + name, variants.put(name, List.of(operations)) == null);
        return this;
    }

    @SuppressWarnings("UseOfSystemOutOrSystemErr")
    private void check(final boolean condition, final String format, final Object... args) {
        if (!condition) {
            System.err.println("ERROR: " + String.format(format, args));
            System.err.println("Usage: " + owner.getName() + " VARIANT...");
            System.err.println("Variants: " + String.join(", ", variants.keySet()));
            System.exit(1);
        }
    }

    public void main(final String... args) {
        check(args.length >= 1, "At least one argument expected, found %s", args.length);
        final List<String> vars = Arrays.stream(args)
                .flatMap(arg -> Arrays.stream(arg.split("[ +]+")))
                .collect(Collectors.toList());
        if (variants.containsKey("Base") && !vars.contains("Base")) {
            vars.add(0, "Base");
        }

        vars.forEach(var -> check(variants.containsKey(var), "Unknown variant '%s'", var));

        final TestCounter counter = new TestCounter(owner, Map.of("variant", String.join("+", vars)));
        vars.forEach(var -> counter.scope("Testing " + var,
                () -> variants.get(var).forEach(variant -> test.accept(variant, counter))));
        counter.printStatus();
    }

    public List<String> getVariants() {
        return List.copyOf(variants.keySet());
    }
}
