package queue;

public class ArrayQueue extends AbstractQueue {
    private Object[] elements = new Object[1];
    private int head = 0;

    private int shift(int index, int shift) {
        int result = index + shift;
        if (result < 0) {
            return result + elements.length;
        } else if (result >= elements.length) {
            return result - elements.length;
        }
        return result;
    }

    private void ensureCapacity() {
        Object[] elements_copy = new Object[2 * elements.length];
        System.arraycopy(elements, head, elements_copy, head, elements.length - head);
        System.arraycopy(elements, 0, elements_copy, elements.length, shift(head, size));
        elements = elements_copy;
    }

    @Override
    public void enqueueImpl(Object element) {
        assert(element != null);
        if (elements.length == size) {
            ensureCapacity();
        }
        elements[shift(head, size)] = element;
    }

    @Override
    public void pushImpl(Object element) {
        assert(element != null);
        if (elements.length == size) {
            ensureCapacity();
        }
        head = shift(head, -1);
        elements[head] = element;
    }

    @Override
    public Object element() {
        assert(size > 0);
        return elements[head];
    }

    @Override
    public Object peek() {
        assert(size > 0);
        return elements[shift(head, size - 1)];
    }

    @Override
    public void dequeueImpl() {
        assert(size > 0);
        elements[head] = null;
        head = shift(head, 1);
    }

    @Override
    public void removeImpl() {
        assert(size > 0);
        final int tail = shift(head, size - 1);
        elements[tail] = null;
    }

    @Override
    public int indexOf(Object element) {
        assert(element != null);
        for (int i = 0; i < size; i++) {
            if (element.equals(elements[shift(head, i)])) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object element) {
        assert(element != null);
        for (int i = 1; i <= size; i++) {
            if (element.equals(elements[shift(head, size - i)])) {
                return size - i;
            }
        }
        return -1;
    }
}
