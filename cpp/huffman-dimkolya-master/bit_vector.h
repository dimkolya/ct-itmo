//
// Created by dimkolya on 03.09.22.
//

#ifndef HUFFMAN_BIT_VECTOR_H
#define HUFFMAN_BIT_VECTOR_H

#include <cstddef>
#include <string>

struct bit_vector {
  friend struct buffered_writer;
  bit_vector();
  explicit bit_vector(const bit_vector& other);
  bit_vector& operator=(const bit_vector& other);
  ~bit_vector();
  void push_back(bool bit);
  void pop_back();
  std::size_t get_shift() const;
  std::string to_string() const;

private:
  std::size_t capacity;
  std::size_t full_chars;
  std::size_t shift;
  unsigned char* data;
};

#endif // HUFFMAN_BIT_VECTOR_H
