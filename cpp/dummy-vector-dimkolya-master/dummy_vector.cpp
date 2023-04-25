#pragma once
#include "dummy_vector.h"

dummy_vector::dummy_vector() : data_(nullptr), size_(0), capacity_(0) {};

dummy_vector::dummy_vector(dummy_vector const& other) : dummy_vector() {
  if (other.size_ != 0) {
    data_ = static_cast<T*>(operator new(other.size_ * sizeof(T)));
    capacity_ = other.size_;
  }
  copy_range(data_, other.data_, other.size_);
  size_ = other.size_;
}

dummy_vector& dummy_vector::operator=(dummy_vector const& other) {
  dummy_vector temp(other);
  dummy_vector::swap(temp);
  return *this;
}
dummy_vector::~dummy_vector() {
  destruct(begin(), end());
  operator delete(data_);
  size_ = capacity_ = 0;
  data_ = nullptr;
}

dummy_vector::T& dummy_vector::operator[](size_t i) {
  return data_[i];
}
dummy_vector::T const& dummy_vector::operator[](size_t i) const {
  return data_[i];
}

dummy_vector::T* dummy_vector::data() {
  return data_;
}
dummy_vector::T const* dummy_vector::data() const {
  return data_;
}
size_t dummy_vector::size() const {
  return size_;
}

dummy_vector::T& dummy_vector::front() {
  return data_[0];
}
dummy_vector::T const& dummy_vector::front() const {
  return data_[0];
}

dummy_vector::T& dummy_vector::back() {
  return data_[size_ - 1];
}
dummy_vector::T const& dummy_vector::back() const {
  return data_[size_ - 1];
}

void dummy_vector::push_back(T const& value) {
  if (size_ == capacity_) {
    push_back_realloc(increase_capacity(), value);
  } else {
    new(data_ + size_) T(value);
  }
  ++size_;
}

void dummy_vector::pop_back() {
  data_[--size_].~T();
}

bool dummy_vector::empty() const {
  return size_ == 0;
}

size_t dummy_vector::capacity() const {
  return capacity_;
}
void dummy_vector::reserve(size_t new_cap) {
  if (new_cap > capacity_) {
    realloc(new_cap);
  }
}
void dummy_vector::shrink_to_fit() {
  if (empty()) {
    operator delete(data_);
    data_ = nullptr;
    capacity_ = 0;
  }
  if (capacity_ > size_) {
    realloc(size_);
  }
}

void dummy_vector::clear() {
  destruct(begin(), end());
  size_ = 0;
}

void dummy_vector::swap(dummy_vector& other) {
  std::swap(data_, other.data_);
  std::swap(size_, other.size_);
  std::swap(capacity_, other.capacity_);
}

dummy_vector::iterator dummy_vector::begin() {
  return data_;
}
dummy_vector::iterator dummy_vector::end() {
  return data_ + size_;
}

dummy_vector::const_iterator dummy_vector::begin() const {
  return data_;
}
dummy_vector::const_iterator dummy_vector::end() const {
  return data_ + size_;
}

dummy_vector::iterator dummy_vector::insert(const_iterator pos, T const& value) {
  size_t count = pos - begin();
  if (size_ == capacity_) {
    reserve(increase_capacity());
  }
  iterator temp_pos = begin() + count;
  push_back(value);
  for (iterator it = end() - 1; it != temp_pos; it--) {
    std::swap(*it, *(it - 1));
  }
  return temp_pos;
}

dummy_vector::iterator dummy_vector::erase(const_iterator pos) {
  return erase(pos, pos + 1);
}

dummy_vector::iterator dummy_vector::erase(const_iterator first,
               const_iterator last) {
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

void dummy_vector::destruct(T* begin, T* end) {
  for (T* it = begin; it != end; ++it) {
    it->~T();
  }
}

size_t dummy_vector::increase_capacity() {
  return (capacity_ == 0) ? 1 : capacity_ * 2;
}

void dummy_vector::copy_range(T* begin, T* copying_begin, size_t count) {
  for (size_t it = 0; it != count; ++it) {
    new(begin + it) T(copying_begin[it]);
  }
}

void dummy_vector::realloc(size_t new_capacity) {
  T* new_data = static_cast<T*>(operator new(new_capacity * sizeof(T)));
  copy_range(new_data, data_, size_);
  destruct(begin(), end());
  operator delete(data_);
  data_ = new_data;
  capacity_ = new_capacity;
}
void dummy_vector::push_back_realloc(size_t new_capacity, const T& value) {
  T* new_data = static_cast<T*>(operator new(new_capacity * sizeof(T)));
  copy_range(new_data, data_, size_);
  new (new_data + size_) T(value);
  destruct(begin(), end());
  operator delete(data_);
  data_ = new_data;
  capacity_ = new_capacity;
}
