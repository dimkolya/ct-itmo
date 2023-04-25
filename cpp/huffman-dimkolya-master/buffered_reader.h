//
// Created by dimkolya on 02.09.22.
//

#ifndef HUFFMAN_BUFFERED_READER_H
#define HUFFMAN_BUFFERED_READER_H

#include <istream>

struct buffered_reader {
  explicit buffered_reader(std::istream& source);

  ~buffered_reader();

  unsigned char next();

  bool eof();

  void reset();

private:
  static const std::streamsize BUFFER_SIZE = 1024;

  std::istream& source;
  std::size_t pos;
  std::streamsize read;
  unsigned char buffer[BUFFER_SIZE];

  void update_buffer();
};

#endif // HUFFMAN_BUFFERED_READER_H
