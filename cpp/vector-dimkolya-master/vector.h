#pragma once

#include <cstddef>
#include <algorithm>

template <typename T>
struct vector {
  using iterator = T*;
  using const_iterator = T const*;

  vector() : data_(nullptr), size_(0), capacity_(0) {}
  vector(vector const& other) : vector() {
    if (other.size_ != 0) {
      data_ = static_cast<T*>(operator new(other.size_ * sizeof(T)));
    }
    copy_range(data_, other.data_, other.size_);
    size_ = other.size_;
    capacity_ = other.size_;
  } // O(N) strong
  vector& operator=(vector const& other) {
    if (&other == this) {
      return *this;
    }
    vector temp(other);
    vector::swap(temp);
    return *this;
  } // O(N) strong

  ~vector() {
    destruct(begin(), end());
    operator delete(data_);
    size_ = capacity_ = 0;
  }

  T& operator[](size_t i) {
    return data_[i];
  }
  T const& operator[](size_t i) const {
    return data_[i];
  }

  T* data() {
    return data_;
  }
  T const* data() const {
    return data_;
  }
  size_t size() const {
    return size_;
  }

  T& front() {
    return *data_;
  }
  T const& front() const {
    return *data_;
  }

  T& back() {
    return data_[size_ - 1];
  }
  T const& back() const {
    return data_[size_ - 1];
  }
  void push_back(T const& value) {
    if (size_ == capacity_) {
      push_back_realloc(increase_capacity(), value);
    } else {
      new (data_ + size_) T(value);
    }
    ++size_;
  } // O(1)* strong
  void pop_back() {
    data_[--size_].~T();
  }

  bool empty() const {
    return size_ == 0;
  }

  size_t capacity() const {
    return capacity_;
  }
  void reserve(size_t new_cap) {
    if (new_cap > capacity_) {
      realloc(new_cap);
    }
  } // O(N) strong
  void shrink_to_fit() {
    if (empty()) {
      operator delete(data_);
      data_ = nullptr;
      capacity_ = 0;
    }
    if (capacity_ > size_) {
      realloc(size_);
    }
  } // O(N) strong

  void clear() {
    destruct(begin(), end());
    size_ = 0;
  }

  void swap(vector& other) {
    std::swap(data_, other.data_);
    std::swap(size_, other.size_);
    std::swap(capacity_, other.capacity_);
  }

  iterator begin() {
    return data_;
  }
  iterator end() {
    return data_ + size_;
  }

  const_iterator begin() const {
    return data_;
  }
  const_iterator end() const {
    return data_ + size_;
  }

  iterator insert(const_iterator pos, T const& value) {
    size_t count = pos - begin();
    push_back(value);
    iterator temp_pos = begin() + count;
    for (iterator it = end() - 1; it != temp_pos; it--) {
      std::swap(*it, *(it - 1));
    }
    return temp_pos;
  } // O(N) strong
  
  iterator erase(const_iterator pos) {
    return erase(pos, pos + 1);
  }

  iterator erase(const_iterator first, const_iterator last) {
    iterator it_first = begin() + (first - begin());
    iterator it_last = begin() + (last - begin());
    while (it_last != end()) {
      std::swap(*it_first, *it_last);
      ++it_first;
      ++it_last;
    }
    destruct(it_first, it_last);
    size_ -= last - first;
    return begin() + (first - begin());
  }

private:
  T* data_;
  size_t size_;
  size_t capacity_;

  void destruct(T* const begin, T* const end) {
    for (T* it = end; it != begin;) {
      (--it)->~T();
    }
  }

  size_t increase_capacity() {
    return (capacity_ == 0) ? 1 : capacity_ * 2;
  }

  void copy_range(T* const begin, T* const copying_begin, size_t const count) {
    for (size_t it = 0; it != count; ++it) {
      try {
        new (begin + it) T(copying_begin[it]);
      } catch (...) {
        destruct(begin, begin + it);
        throw;
      }
    }
  }

  T* abstract_realloc(size_t const new_capacity) {
    T* new_data = static_cast<T*>(operator new(new_capacity * sizeof(T)));
    try {
      copy_range(new_data, data_, size_);
    } catch (...) {
      operator delete(new_data);
      throw;
    }
    return new_data;
  }
  void realloc(size_t const new_capacity) {
    T* new_data = abstract_realloc(new_capacity);
    destruct(begin(), end());
    operator delete(data_);
    data_ = new_data;
    capacity_ = new_capacity;
  }
  void push_back_realloc(size_t const new_capacity, T const& value) {
    T* new_data = abstract_realloc(new_capacity);
    try {
      new (new_data + size_) T(value);
    } catch (...) {
      destruct(new_data, new_data + size_);
      operator delete(new_data);
      throw;
    }
    destruct(begin(), end());
    operator delete(data_);
    data_ = new_data;
    capacity_ = new_capacity;
  }
};
