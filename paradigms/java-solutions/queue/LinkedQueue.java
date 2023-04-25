package queue;

public class LinkedQueue extends AbstractQueue {
    private Node head = null;
    private Node tail = null;

    @Override
    protected void enqueueImpl(Object element) {
        if (isEmpty()) {
            head = tail = new Node(element, null, null);
        } else {
            tail = new Node(element, tail,null);
            tail.next.prev = tail;
        }
    }

    @Override
    public void pushImpl(Object element) {
        if (isEmpty()) {
            head = tail = new Node(element,null, null);
        } else {
            head = new Node(element, null, head);
            head.prev.next = head;
        }
    }

    @Override
    public void dequeueImpl() {
        head = head.prev;
        if (head == null) {
            tail = null;
        }
    }

    @Override
    protected void removeImpl() {
        tail = tail.next;
        if (tail == null) {
            head = null;
        }
    }

    @Override
    public int indexOf(Object element) {
        Node iterator = head.makeCopy();
        int count = 0;
        while (iterator != null) {
            if (element.equals(iterator.element)) {
                return count;
            }
            count++;
            iterator = iterator.prev;
        }
        return -1;
    }

    @Override
    public int lastIndexOf(Object element) {
        Node iterator = tail.makeCopy();
        int count = size - 1;
        while (iterator != null) {
            if (element.equals(iterator.element)) {
                return count;
            }
            count--;
            iterator = iterator.next;
        }
        return -1;
    }

    @Override
    public Object element() {
        return head.element;
    }

    @Override
    public Object peek() {
        return null;
    }

    static class Node {
        private final Object element;
        private Node next;
        private Node prev;

        public Node(Object element, Node next, Node prev) {
            this.element = element;
            this.next = next;
            this.prev = prev;
        }

        public Node makeCopy() {
            return new Node(element, next, prev);
        }
    }
}
