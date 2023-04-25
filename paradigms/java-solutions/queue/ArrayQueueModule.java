package queue;

public class ArrayQueueModule {
    private static int size = 0;
    private static Object[] elements = new Object[1];
    private static int head = 0;

    /*
    Model: a[0]..a[n - 1]
    Invariant: ∀i ∈ [0 : n - 1]: a[i] != null

    Let immutable(n): ∀i ∈ [0 : n - 1]: a'[i] = a[i]
     */

    private static int shift(int index, int shift) {
        final int result = index + shift;
        if (result < 0) {
            return result + elements.length;
        } else if (result >= elements.length) {
            return result - elements.length;
        }
        return result;
    }

    private static void ensureCapacity() {
        Object[] elements_copy = new Object[2 * elements.length];
        final int tail = shift(head, size);
        if (tail > head) {
            System.arraycopy(elements, head, elements_copy, head, size);
        } else {
            System.arraycopy(elements, head, elements_copy, head, elements.length - head);
            System.arraycopy(elements, 0, elements_copy, elements.length, tail);
        }
        elements = elements_copy;
    }

    //  Pred: element != null
    //  Post: n' == n + 1 && a[n'] == element && immutable(n)
    //  enqueue(element)
    public static void enqueue(Object element) {
        assert(element != null);
        if (elements.length == size) {
            ensureCapacity();
        }
        elements[shift(head, size)] = element;
        size++;
    }

    //  Pred: element != null
    //  Post: n' == n + 1 && a[0] == element && ∀i ∈ [1 : n]: a'[i] = a[i - 1]
    //  push(element)
    public static void push(Object element) {
        assert(element != null);
        if (elements.length == size) {
            ensureCapacity();
        }
        head = shift(head, -1);
        elements[head] = element;
        size++;
    }

    //  Pred: n > 0
    //  Post: R = a[0] && n' == n && immutable(n)
    //  element()
    public static Object element() {
        assert(size > 0);
        return elements[head];
    }

    //  Pred: n > 0
    //  Post: R = a[n - 1] && n' == n && immutable(n)
    //  peek()
    public static Object peek() {
        assert(size > 0);
        return elements[shift(head, size - 1)];
    }

    //  Pred: n > 0
    //  Post: R = a[0] && n' == n - 1 && ∀i ∈ [0 : n - 2]: a'[i] = a[i + 1]
    //  dequeue()
    public static Object dequeue() {
        assert(size > 0);
        final Object result = elements[head];
        elements[head] = null;
        head = shift(head, 1);
        size--;
        return result;
    }

    //  Pred: n > 0
    //  Post: R = a[n - 1] && n' == n - 1 && immutable(n - 1)
    //  remove()
    public static Object remove() {
        assert(size > 0);
        final int tail = shift(head, size - 1);
        final Object result = elements[tail];
        elements[tail] = null;
        size--;
        return result;
    }

    //  Pred: element != null
    //  Post: R = i, ∃ i = min[0 : n - 1]: (a[i] == element)
    //        R = -1, else
    //          && n' == n && immutable(n)
    public static int indexOf(Object element) {
        assert(element != null);
        for (int i = 0; i < size; i++) {
            if (element.equals(elements[shift(head, i)])) {
                return i;
            }
        }
        return -1;
    }

    //  Pred: element != null
    //  Post: R = i, ∃ i = max[0 : n - 1]: (a[i] == element)
    //        R = -1, else
    //          && n' == n && immutable(n)
    public static int lastIndexOf(Object element) {
        assert(element != null);
        for (int i = 1; i <= size; i++) {
            if (element.equals(elements[shift(head, size - i)])) {
                return size - i;
            }
        }
        return -1;
    }

    //  Pred: true
    //  Post: R = n && n' == n && immutable(n)
    //  size()
    public static int size() {
        return size;
    }


    //  Pred: true
    //  Post: R = (n == 0) && n' == n && immutable(n)
    //  isEmpty()
    public static boolean isEmpty() {
        return size == 0;
    }

    //  Pred: true
    //  Post: n == 0
    //  clear()
    public static void clear() {
        elements = new Object[1];
        size = 0;
        head = 0;
    }
}
