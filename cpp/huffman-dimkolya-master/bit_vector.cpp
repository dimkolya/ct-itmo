//
// Created by dimkolya on 03.09.22.
//

#include "bit_vector.h"

#include <climits>

bit_vector::bit_vector()
    : capacity(0), full_chars(0), shift(0), data(nullptr) {}

bit_vector::bit_vector(const bit_vector& other) : bit_vector() {
  full_chars = other.full_chars;
  shift = other.shift;
  capacity = other.full_chars + ((shift != 0) ? 1 : 0);
  if (capacity != 0) {
    data = static_cast<unsigned char*>(operator new(capacity));
    for (std::size_t i = 0; i < capacity; ++i) {
      data[i] = other.data[i];
    }
  } else {
    data = nullptr;
  }
}

bit_vector& bit_vector::operator=(const bit_vector& other) {
  if (this == &other) {
    return *this;
  }
  bit_vector temp(other);
  std::swap(temp.capacity, capacity);
  std::swap(temp.full_chars, full_chars);
  std::swap(temp.shift, shift);
  std::swap(temp.data, data);
  return *this;
}

bit_vector::~bit_vector() {
  operator delete(data);
}

void bit_vector::push_back(bool bit) {
  if (full_chars == capacity) {
    unsigned char* temp_data =
        static_cast<unsigned char*>(operator new(++capacity));
    for (std::size_t i = 0; i < full_chars; ++i) {
      temp_data[i] = data[i];
    }
    operator delete(data);
    data = temp_data;
    data[full_chars] = ((bit ? 1 : 0) << (CHAR_BIT - 1 - shift));
    shift = 1;
  } else {
    data[full_chars] |= ((bit ? 1 : 0) << (CHAR_BIT - 1 - shift));
    if (shift == CHAR_BIT - 1) {
      shift = 0;
      ++full_chars;
    } else {
      ++shift;
    }
  }
}

void bit_vector::pop_back() {
  if (shift == 0) {
    data[--full_chars] &= ~1;
    shift = CHAR_BIT - 1;
  } else {
    data[full_chars] &= (~(1 << (CHAR_BIT - shift)));
    --shift;
  }
}

std::size_t bit_vector::get_shift() const {
  return shift;
}

std::string bit_vector::to_string() const {
  std::string ans;
  for (size_t i = 0; i < full_chars; ++i) {
    for (size_t j = 0; j < CHAR_BIT; ++j) {
      ans.push_back(((data[i] & (1 << (CHAR_BIT - 1 - j))) != 0) ? '1' : '0');
    }
  }
  for (size_t j = 0; j < shift; ++j) {
    ans.push_back(((data[full_chars] & (1 << (CHAR_BIT - 1 - j))) != 0) ? '1'
                                                                        : '0');
  }
  return ans;
}
