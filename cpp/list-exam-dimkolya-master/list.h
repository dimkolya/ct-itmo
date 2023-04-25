#pragma once
#include <cassert>
#include <iterator>

template <typename T>
class list {
private:
  struct base_node {
    base_node* prev;
    base_node* next;

    base_node() : prev(this), next(this) {}
  };
  struct node : base_node {
    T key;

    explicit node(T const& key) : key(key){};
  };
public:
  template <typename V>
  struct list_iterator : std::iterator<std::bidirectional_iterator_tag, V> {
    friend class list;
    friend struct list_iterator<const V>;
    list_iterator() : ptr(nullptr) {}
    list_iterator(list_iterator const &other) : ptr(other.ptr) {}
    template <typename U>
    list_iterator(
        list_iterator<U> const& other,
        typename std::enable_if<std::is_same<V, const U>::value>::type* =
            nullptr) : ptr(other.ptr) {}

    list_iterator& operator=(const list_iterator& other) = default;

    friend bool operator==(const list_iterator& a, const list_iterator& b) {
      return a.ptr == b.ptr;
    }
    friend bool operator!=(const list_iterator& a, const list_iterator& b) {
      return a.ptr != b.ptr;
    }

    V& operator*() const {
      return static_cast<node*>(ptr)->key;
    }
    V* operator->() const {
      return &(static_cast<node*>(ptr)->key);
    }

    list_iterator& operator++() {
      ptr = ptr->next;
      return *this;
    }
    list_iterator operator++(int) {
      list_iterator temp(*this);
      ++(*this);
      return temp;
    }
    list_iterator& operator--() {
      ptr = ptr->prev;
      return *this;
    }
    list_iterator operator--(int) {
      list_iterator temp(*this);
      --(*this);
      return temp;
    }

    void swap(list_iterator const& other) {
      std::swap(ptr, other.ptr);
    }

  private:
    base_node* ptr;

    list_iterator(base_node *a) : ptr(a) {}
  };

  // bidirectional iterator
  using iterator = list_iterator<T>;
  // bidirectional iterator
  using const_iterator = list_iterator<const T>;
  using reverse_iterator = std::reverse_iterator<iterator>;
  using const_reverse_iterator = std::reverse_iterator<const_iterator>;

  // O(1)
  list() noexcept : fake() {}
  // O(n), strong
  list(list const& other) : list() {
    for (const_iterator it = other.begin(); it != other.end(); ++it) {
      push_back(*it);
    }
  }

  // O(n), strong
  list& operator=(list const& other) {
    if (this != &other) {
      list temp(other);
      swap(*this, temp);
    }
    return *this;
  }

  // O(n)
  ~list() {
    clear();
  }

  // O(1)
  bool empty() const noexcept {
    return fake.next == &fake;
  }

  // O(1)
  T& front() noexcept {
    return *begin();
  }
  // O(1)
  T const& front() const noexcept {
    return *begin();
  }

  // O(1), strong
  void push_front(T const& key) {
    insert(begin(), key);
  }
  // O(1)
  void pop_front() noexcept {
    erase(begin());
  }

  // O(1)
  T& back() noexcept {
    return static_cast<node*>(fake.prev)->key;
  }
  // O(1)
  T const& back() const noexcept {
    return static_cast<node*>(fake.prev)->key;
  }

  // O(1), strong
  void push_back(T const& key) {
    insert(end(), key);
  }
  // O(1)
  void pop_back() noexcept {
    erase(--end());
  }

  // O(1)
  iterator begin() noexcept {
    return iterator(fake.next);
  }
  // O(1)
  const_iterator begin() const noexcept {
    return const_iterator(fake.next);
  }

  // O(1)
  iterator end() noexcept {
    return iterator(&fake);
  }
  // O(1)
  const_iterator end() const noexcept {
    return const_iterator(const_cast<base_node*>(&fake));
  }

  // O(1)
  reverse_iterator rbegin() noexcept {
    return reverse_iterator(end());
  }
  // O(1)
  const_reverse_iterator rbegin() const noexcept {
    return const_reverse_iterator(end());
  }

  // O(1)
  reverse_iterator rend() noexcept {
    return reverse_iterator(begin());
  }
  // O(1)
  const_reverse_iterator rend() const noexcept {
    return const_reverse_iterator(begin());
  }

  // O(n)
  void clear() noexcept {
    while (!empty()) {
      pop_back();
    }
  }

  // O(1), strong
  iterator insert(const_iterator pos, T const& val) {
    node* temp = new node(val);
    temp->prev = pos.ptr->prev;
    temp->next = pos.ptr;
    pos.ptr->prev->next = temp;
    pos.ptr->prev = temp;
    return iterator(temp);
  }
  // O(1)
  iterator erase(const_iterator pos) noexcept {
    iterator result(pos.ptr->next);
    pos.ptr->next->prev = pos.ptr->prev;
    pos.ptr->prev->next = pos.ptr->next;
    delete static_cast<node*>(pos.ptr);
    return result;
  }
  // O(n)
  iterator erase(const_iterator first, const_iterator last) noexcept {
    while (first != last) {
      first = erase(first);
    }
    return iterator(first.ptr);
  }
  // O(1)
  void splice(const_iterator pos, list& other, const_iterator first,
              const_iterator last) noexcept {
    node* temp = static_cast<node*>(first.ptr->prev);
    link(pos.ptr->prev, first.ptr);
    link(last.ptr->prev, pos.ptr);
    link(temp, last.ptr);
  }

  friend void swap(list& a, list& b) noexcept {
    std::swap(a.fake, b.fake);
    if (b.fake.next == &a.fake) {
      b.fake.prev = b.fake.next = &b.fake;
    } else {
      b.fake.prev->next = &b.fake;
      b.fake.next->prev = &b.fake;
    }
    if (a.fake.next == &b.fake) {
      a.fake.prev = a.fake.next = &a.fake;
    } else {
      a.fake.prev->next = &a.fake;
      a.fake.next->prev = &a.fake;
    }
  }

private:
  base_node fake;

  void link(base_node *a, base_node *b) {
    a->next = b;
    b->prev = a;
  }
};
