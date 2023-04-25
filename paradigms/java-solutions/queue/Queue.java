package queue;

import java.util.function.Predicate;

public interface Queue {
    /*
    Model: a[0]..a[n - 1]
    Invariant: ∀i ∈ [0 : n - 1]: a[i] != null

    Let immutable(n): ∀i ∈ [0 : n - 1]: a'[i] = a[i]
     */

    //  Pred: element != null
    //  Post: n' == n + 1 && a[n'] == element && immutable(n)
    //  enqueue(element)
    void enqueue(Object element);

    //  Pred: element != null
    //  Post: n' == n + 1 && a[0] == element && ∀i ∈ [1 : n]: a'[i] = a[i - 1]
    //  push(element)
    void push(Object element);

    //  Pred: n > 0
    //  Post: R = a[0] && n' == n && immutable(n)
    //  element()
    Object element();

    //  Pred: n > 0
    //  Post: R = a[n - 1] && n' == n && immutable(n)
    //  peek()
    Object peek();

    //  Pred: n > 0
    //  Post: R = a[0] && n' == n - 1 && ∀i ∈ [0 : n - 2]: a'[i] = a[i + 1]
    //  dequeue()
    Object dequeue();

    //  Pred: n > 0
    //  Post: R = a[n - 1] && n' == n - 1 && immutable(n - 1)
    //  remove()
    Object remove();

    //  Pred: element != null
    //  Post: R = i, ∃ i = min[0 : n - 1]: (a[i] == element)
    //        R = -1, else
    //          && n' == n && immutable(n)
    int indexOf(Object element);

    //  Pred: element != null
    //  Post: R = i, ∃ i = max[0 : n - 1]: (a[i] == element)
    //        R = -1, else
    //          && n' == n && immutable(n)
    int lastIndexOf(Object element);

    //  Pred: true
    //  Post: a' = a[i_1, ..., i_k]: {i_k} is subsequence of [0 : n - 1]: ∀i ∉ {i_k} <=> predicate.test(a[i]) == true
    //          && n' == k
    void removeIf(Predicate<Object> predicate);

    //  Pred: true
    //  Post: a' = a[i_1, ..., i_k]: {i_k} is subsequence of [0 : n - 1]: ∀i ∉ {i_k} <=> predicate.test(a[i]) == false
    //          && n' == k
    void retainIf(Predicate<Object> predicate);

    //  Pred: true
    //  Post: a' = a[0 : k]: k ∈ [0 : n - 1]: ∀i ∈ [0 : k] predicate.test(a[i]) == true
    //          && n' == k
    void takeWhile(Predicate<Object> predicate);

    //  Pred: true
    //  Post: (a' = a[k : n - 1]: k ∈ [1 : n - 1]: ∀i ∈ [0 : k - 1] predicate.test(a[i]) == true
    //          || k == 0: predicate.test(a[0]) == false)
    //          && n' == n - k + 1
    void dropWhile(Predicate<Object> predicate);

    //  Pred: true
    //  Post: R = n && n' == n && immutable(n)
    //  size()
    int size();


    //  Pred: true
    //  Post: R = (n == 0) && n' == n && immutable(n)
    //  isEmpty()
    boolean isEmpty();

    //  Pred: true
    //  Post: n == 0
    //  clear()
    void clear();
}
