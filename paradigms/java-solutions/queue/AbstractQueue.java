package queue;

import java.util.function.Predicate;

public abstract class AbstractQueue implements Queue {
    protected int size;

    protected abstract void enqueueImpl(Object element);

    @Override
    public void enqueue(Object element) {
        assert(element != null);
        enqueueImpl(element);
        size++;
    }

    protected abstract void pushImpl(Object element);

    @Override
    public void push(Object element) {
        assert(element != null);
        pushImpl(element);
        size++;
    }

    protected abstract void dequeueImpl();

    @Override
    public Object dequeue() {
        assert(size > 0);
        final Object result = element();
        dequeueImpl();
        size--;
        return result;
    }

    protected abstract void removeImpl();

    @Override
    public Object remove() {
        assert(size > 0);
        final Object result = peek();
        removeImpl();
        size--;
        return result;
    }

    @Override
    public void retainIf(Predicate<Object> predicate) {
        int n = size;
        for (int i = 0; i < n; i++) {
            Object element = dequeue();
            if (predicate.test(element)) {
                enqueue(element);
            }
        }
    }

    @Override
    public void removeIf(Predicate<Object> predicate) {
        retainIf(predicate.negate());
    }

    private void abstractWhile(Predicate<Object> predicate, boolean take) {
        int count = size;
        while (count > 0 && predicate.test(element())) {
            count--;
            if (take) {
                enqueue(dequeue());
            } else {
                dequeue();
            }
        }
        if (take) {
            for (int i = 0; i < count; i++) {
                dequeue();
            }
        }
    }

    @Override
    public void takeWhile(Predicate<Object> predicate) {
        abstractWhile(predicate, true);
    }

    @Override
    public void dropWhile(Predicate<Object> predicate) {
        abstractWhile(predicate, false);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        while (!isEmpty()) {
            dequeue();
        }
    }
}
