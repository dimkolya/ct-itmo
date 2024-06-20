import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class IterativeParallelism implements ScalarIP {
    private <T, R> R common(int threads, List<? extends T> values,
                            Function<Stream<? extends T>, R> threadReducer,
                            Function<Stream<R>, R> reducer) throws InterruptedException {
        threads = Math.min(threads, values.size());
        int quotient = values.size() / threads;
        int remainder = values.size() % threads;
        List<List<? extends T>> threadViews = new ArrayList<>(Collections.nCopies(threads, null));
        for (int i = 0, pos = 0; i < threads; i++) {
            int size = quotient + (i < remainder ? 1 : 0);
            threadViews.set(i, values.subList(pos, pos + size));
            pos += size;
        }
        List<R> threadResults = new ArrayList<>(Collections.nCopies(threads, null));
        Thread[] threadList = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            final int index = i;
            threadList[i] = new Thread(() -> {
                threadResults.set(index, threadReducer.apply(threadViews.get(index).stream()));
            });
            threadList[i].start();
        }
        for (Thread thread : threadList) {
            thread.join();
        }
        return reducer.apply(threadResults.stream());
    }


    @Override
    public <T> T maximum(int threads, List<? extends T> values, Comparator<? super T> comparator) throws InterruptedException {
        return common(threads, values,
                stream -> stream.max(comparator).orElseThrow(),
                stream -> stream.max(comparator).orElseThrow());
    }

    @Override
    public <T> T minimum(int threads, List<? extends T> values, Comparator<? super T> comparator) throws InterruptedException {
        return maximum(threads, values, comparator.reversed());
    }

    @Override
    public <T> boolean all(int threads, List<? extends T> values, Predicate<? super T> predicate) throws InterruptedException {
        return count(threads, values, predicate) == values.size();
    }

    @Override
    public <T> boolean any(int threads, List<? extends T> values, Predicate<? super T> predicate) throws InterruptedException {
        return count(threads, values, predicate) > 0;
    }

    @Override
    public <T> int count(int threads, List<? extends T> values, Predicate<? super T> predicate) throws InterruptedException {
        return common(threads, values,
                stream -> (int) stream.filter(predicate).count(),
                stream -> stream.reduce(Integer::sum).orElse(0));
    }
}
