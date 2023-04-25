#include <cstddef>
#include <new>

template <typename T, std::size_t SMALL_SIZE>
struct socow_vector {
  using iterator = T*;
  using const_iterator = const T*;

  socow_vector() : size_(0), is_big(false) {}

  socow_vector(const socow_vector& other)
      : size_(other.size_), is_big(other.is_big) {
    if (is_big) {
      big = other.big;
      ++(big->ref_count_);
    } else {
      for (std::size_t i = 0; i < size_; ++i) {
        new (small_data_ + i) T(other.small_data_[i]);
      }
    }
  }

  socow_vector& operator=(const socow_vector& other) {
    if (this == &other) {
      return *this;
    }
    socow_vector temp(other);
    socow_vector::swap(temp);
    return *this;
  }

  ~socow_vector() {
    if (is_big) {
      unshare();
    } else {
      destruct(small_data_, small_data_ + size_);
    }
  }

  bool empty() const {
    return size_ == 0;
  }

  std::size_t size() const {
    return size_;
  }

  void swap(socow_vector& other) {
    if (is_big && other.is_big) {
      std::swap(big, other.big);
    } else if (is_big && !other.is_big) {
      swap_big_and_small(*this, other);
    } else if (!is_big && other.is_big) {
      swap_big_and_small(other, *this);
    } else {
      for (std::size_t i = 0; i < std::min(size_, other.size_); ++i) {
        std::swap(small_data_[i], other.small_data_[i]);
      }
      if (size_ < other.size_) {
        copy_range(small_data_ + size_, other.small_data_ + size_,
                   other.size_ - size_);
        destruct(other.small_data_ + size_, other.small_data_ + other.size_);
      } else if (size_ > other.size_) {
        copy_range(other.small_data_ + other.size_, small_data_ + other.size_,
                   size_ - other.size_);
        destruct(small_data_ + other.size_, small_data_ + size_);
      }
    }
    std::swap(size_, other.size_);
  }

  void push_back(const T& value) {
    if (is_big) {
      if (size_ == big->capacity_) {
        dynamic* temp = push_back_realloc(increase_capacity(), value);
        unshare();
        big = temp;
      } else if (big->ref_count_ > 1) {
        dynamic* temp = push_back_realloc(big->capacity_, value);
        --(big->ref_count_);
        big = temp;
      } else {
        new (big->data_ + size_) T(value);
      }
    } else {
      if (size_ == SMALL_SIZE) {
        dynamic* temp = push_back_realloc(SMALL_SIZE + 1, value);
        destruct(small_data_, small_data_ + SMALL_SIZE);
        big = temp;
        is_big = true;
      } else {
        new (small_data_ + size_) T(value);
      }
    }
    ++size_;
  }

  void pop_back() {
    data()[--size_].~T();
  }

  const T& operator[](std::size_t pos) const {
    return data()[pos];
  }
  T& operator[](std::size_t pos) {
    return data()[pos];
  }

  T* data() {
    if (is_big) {
      if (big->ref_count_ == 1) {
        return big->data_;
      }
      realloc(big->capacity_);
      return big->data_;
    } else {
      return small_data_;
    }
  }
  const T* data() const {
    if (is_big) {
      return big->data_;
    } else {
      return small_data_;
    }
  }

  T& front() {
    return *data();
  }
  const T& front() const {
    return *data();
  }

  T& back() {
    return data()[size_ - 1];
  }
  const T& back() const {
    return data()[size_ - 1];
  }

  void reserve(std::size_t new_cap) {
    if (is_big && ((big->ref_count_ > 1 && new_cap > size_) ||
                   new_cap > big->capacity_)) {
      realloc(new_cap);
    } else if (!is_big && new_cap > SMALL_SIZE) {
      dynamic* temp = abstract_realloc(new_cap);
      destruct(small_data_, small_data_ + size_);
      big = temp;
      is_big = true;
    }
  }

  std::size_t capacity() const {
    if (is_big) {
      return big->capacity_;
    } else {
      return SMALL_SIZE;
    }
  }

  void shrink_to_fit() {
    if (is_big) {
      if (size_ <= SMALL_SIZE) {
        dynamic* temp = big;
        try {
          copy_range(small_data_, temp->data_, size_);
        } catch (...) {
          big = temp;
          throw;
        }
        if (temp->ref_count_ == 1) {
          destruct(temp->data_, temp->data_ + size_);
          operator delete(temp);
        } else {
          --(temp->ref_count_);
        }
        is_big = false;
        return;
      }
      if (size_ == big->capacity_) {
        return;
      }
      realloc(size_);
    }
  }

  void clear() {
    if (size_ == 0) {
      return;
    }
    if (is_big) {
      if (big->ref_count_ == 1) {
        destruct(big->data_, big->data_ + size_);
      } else {
        dynamic* temp = new_dynamic(big->capacity_);
        --(big->ref_count_);
        big = temp;
      }
    } else {
      destruct(small_data_, small_data_ + size_);
    }
    size_ = 0;
  }

  iterator begin() {
    return data();
  }
  const_iterator begin() const {
    return data();
  }

  iterator end() {
    return data() + size_;
  }
  const_iterator end() const {
    return data() + size_;
  }

  iterator insert(const_iterator pos, const T& value) {
    std::size_t count = pos - const_data();
    push_back(value);
    iterator temp_pos = const_data() + count;
    for (iterator it = end() - 1; it != temp_pos; --it) {
      std::swap(*it, *(it - 1));
    }
    return temp_pos;
  }

  iterator erase(const_iterator pos) {
    return erase(pos, pos + 1);
  }

  iterator erase(const_iterator first, const_iterator last) {
    std::size_t count = last - first;
    std::size_t first_ind = first - const_data();
    if (is_big && big->ref_count_ > 1) {
      realloc(big->capacity_);
    }
    T* it_first = const_data() + first_ind;
    while (it_first != end() - count) {
      std::swap(*it_first, *(it_first + count));
      ++it_first;
    }
    destruct(it_first, it_first + count);
    size_ -= count;
    return begin() + first_ind;
  }

private:
  struct dynamic {
    std::size_t capacity_;
    std::size_t ref_count_;
    T data_[0];
  };

  std::size_t size_;
  bool is_big;
  union {
    dynamic* big;
    T small_data_[SMALL_SIZE];
  };

  void copy_range(T* begin, const T* copying_begin, std::size_t count) {
    for (std::size_t i = 0; i < count; ++i) {
      try {
        new (begin + i) T(*(copying_begin + i));
      } catch (...) {
        destruct(begin, begin + i);
        throw;
      }
    }
  }

  void destruct(T* begin, T* end) {
    for (; end != begin;) {
      (--end)->~T();
    }
  }

  std::size_t increase_capacity() {
    return 2 * big->capacity_;
  }

  void unshare() {
    if (big->ref_count_ == 1) {
      destruct(big->data_, big->data_ + size_);
      operator delete(big);
    } else {
      --(big->ref_count_);
    }
  }

  T* const_data() {
    return (is_big ? big->data_ : small_data_);
  }

  void swap_big_and_small(socow_vector& rhs, socow_vector& lhs) {
    dynamic* temp = rhs.big;
    try {
      copy_range(rhs.small_data_, lhs.small_data_, lhs.size_);
    } catch (...) {
      rhs.big = temp;
      throw;
    }
    destruct(lhs.small_data_, lhs.small_data_ + lhs.size_);
    lhs.big = temp;
    std::swap(rhs.is_big, lhs.is_big);
  }

  dynamic* new_dynamic(std::size_t new_cap) {
    auto temp = static_cast<dynamic*>(operator new(sizeof(dynamic) +
                                                   sizeof(T) * new_cap));
    temp->capacity_ = new_cap;
    temp->ref_count_ = 1;
    return temp;
  }
  dynamic* abstract_realloc(std::size_t new_cap) {
    dynamic* temp = new_dynamic(new_cap);
    try {
      copy_range(temp->data_, const_data(), size_);
    } catch (...) {
      operator delete(temp);
      throw;
    }
    return temp;
  }
  void realloc(std::size_t new_cap) {
    dynamic* temp = abstract_realloc(new_cap);
    unshare();
    big = temp;
  }
  dynamic* push_back_realloc(std::size_t new_cap, const T& value) {
    dynamic* temp = abstract_realloc(new_cap);
    try {
      new (temp->data_ + size_) T(value);
    } catch (...) {
      destruct(temp->data_, temp->data_ + size_);
      operator delete(temp);
      throw;
    }
    return temp;
  }
};
