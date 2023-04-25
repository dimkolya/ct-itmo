package queue;

public class ArrayQueueADT {
    private int size = 0;
    private Object[] elements = new Object[1];
    private int head = 0;

    /*
    Model: a[0]..a[n - 1]
    Invariant: ∀i ∈ [0 : n - 1]: a[i] != null

    Let immutable(n): ∀i ∈ [0 : n - 1]: a'[i] = a[i]
     */

    private static int shift(ArrayQueueADT queue, int index, int shift) {
        int result = index + shift;
        if (result < 0) {
            return result + queue.elements.length;
        } else if (result >= queue.elements.length) {
            return result - queue.elements.length;
        }
        return result;
    }

    private static void ensureCapacity(ArrayQueueADT queue) {
        Object[] elements_copy = new Object[2 * queue.elements.length];
        final int tail = shift(queue, queue.head, queue.size);
        if (tail > queue.head) {
            System.arraycopy(queue.elements, queue.head, elements_copy, queue.head, queue.size);
        } else {
            System.arraycopy(queue.elements, queue.head, elements_copy, queue.head, queue.elements.length - queue.head);
            System.arraycopy(queue.elements, 0, elements_copy, queue.elements.length, tail);
        }
        queue.elements = elements_copy;
    }

    //  Pred: element != null && queue != null
    //  Post: n' == n + 1 && a[n'] == element && immutable(n)
    //  enqueue(element)
    public static void enqueue(ArrayQueueADT queue, Object element) {
        assert(element != null);
        if (queue.elements.length == queue.size) {
            ensureCapacity(queue);
        }
        queue.elements[shift(queue, queue.head, queue.size)] = element;
        queue.size++;
    }

    //  Pred: element != null && queue != null
    //  Post: n' == n + 1 && a[0] == element && ∀i ∈ [1 : n]: a'[i] = a[i - 1]
    //  push(element)
    public static void push(ArrayQueueADT queue, Object element) {
        assert(element != null);
        if (queue.elements.length == queue.size) {
            ensureCapacity(queue);
        }
        queue.head = shift(queue, queue.head, -1);
        queue.elements[queue.head] = element;
        queue.size++;
    }

    //  Pred: n > 0 && queue != null
    //  Post: R = a[0] && n' == n && immutable(n)
    //  element()
    public static Object element(ArrayQueueADT queue) {
        assert(queue.size > 0);
        return queue.elements[queue.head];
    }

    //  Pred: n > 0 && queue != null
    //  Post: R = a[n - 1] && n' == n && immutable(n)
    //  peek()
    public static Object peek(ArrayQueueADT queue) {
        assert(queue.size > 0);
        return queue.elements[shift(queue, queue.head, queue.size - 1)];
    }

    //  Pred: n > 0 && queue != null
    //  Post: R = a[0] && n' == n - 1 && ∀i ∈ [0 : n - 2]: a'[i] = a[i + 1]
    //  dequeue()
    public static Object dequeue(ArrayQueueADT queue) {
        assert(queue.size > 0);
        Object result = queue.elements[queue.head];
        queue.elements[queue.head] = null;
        queue.head = shift(queue, queue.head, 1);
        queue.size--;
        return result;
    }

    //  Pred: n > 0 && queue != null
    //  Post: R = a[n - 1] && n' == n - 1 && immutable(n - 1)
    //  remove()
    public static Object remove(ArrayQueueADT queue) {
        assert(queue.size > 0);
        queue.size--;
        final int tail = shift(queue, queue.head, queue.size);
        Object result = queue.elements[tail];
        queue.elements[tail] = null;
        return result;
    }

    //  Pred: element != null && queue != null
    //  Post: R = i, ∃ i = min[0 : n - 1]: (a[i] == element)
    //        R = -1, else
    //          && n' == n && immutable(n)
    //  indexOf()
    public static int indexOf(ArrayQueueADT queue, Object element) {
        assert(element != null);
        for (int i = 0; i < queue.size; i++) {
            if (element.equals(queue.elements[shift(queue, queue.head, i)])) {
                return i;
            }
        }
        return -1;
    }

    //  Pred: element != null && queue != null
    //  Post: R = i, ∃ i = max[0 : n - 1]: (a[i] == element)
    //        R = -1, else
    //          && n' == n && immutable(n)
    //  lastIndexOf()
    public static int lastIndexOf(ArrayQueueADT queue, Object element) {
        assert(element != null);
        for (int i = 1; i <= queue.size; i++) {
            if (element.equals(queue.elements[shift(queue, queue.head, queue.size - i)])) {
                return queue.size - i;
            }
        }
        return -1;
    }

    //  Pred: queue != null
    //  Post: R = n && n' == n && immutable(n)
    //  size()
    public static int size(ArrayQueueADT queue) {
        return queue.size;
    }


    //  Pred: queue != null
    //  Post: R = (n == 0) && n' == n && immutable(n)
    //  isEmpty()
    public static boolean isEmpty(ArrayQueueADT queue) {
        return queue.size == 0;
    }

    //  Pred: queue != null
    //  Post: n == 0
    //  clear()
    public static void clear(ArrayQueueADT queue) {
        queue.elements = new Object[1];
        queue.size = 0;
        queue.head = 0;
    }
}
