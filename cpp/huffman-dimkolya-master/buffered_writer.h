//
// Created by dimkolya on 02.09.22.
//

#ifndef HUFFMAN_BUFFERED_WRITER_H
#define HUFFMAN_BUFFERED_WRITER_H

#include "bit_vector.h"

#include <ostream>

struct buffered_writer {
  explicit buffered_writer(std::ostream& source);

  ~buffered_writer();

  void write(unsigned char c);

  void write(const bit_vector& code);

  void write(bool bit);

private:
  static const std::streamsize BUFFER_SIZE = 1024;

  std::ostream& source;
  std::streamsize written;
  std::size_t written_bits;
  unsigned char buffer[BUFFER_SIZE];

  void write_zero_shift(unsigned char c);

  void char_filled();
};

#endif // HUFFMAN_BUFFERED_WRITER_H
