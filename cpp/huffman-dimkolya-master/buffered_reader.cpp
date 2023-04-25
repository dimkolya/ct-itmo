//
// Created by dimkolya on 02.09.22.
//

#include "buffered_reader.h"

buffered_reader::buffered_reader(std::istream& source)
    : source(source), pos(0), read(0) {}

buffered_reader::~buffered_reader() = default;

unsigned char buffered_reader::next() {
  if (pos == read) {
    update_buffer();
  }
  if (read == 0) {
    return '\0';
  }
  return buffer[pos++];
}

bool buffered_reader::eof() {
  if (pos == read) {
    update_buffer();
  }
  return pos == read;
}

void buffered_reader::reset() {
  source.clear();
  source.seekg(0, std::ios::beg);
  pos = 0;
  read = 0;
}

void buffered_reader::update_buffer() {
  source.read(reinterpret_cast<char*>(buffer), BUFFER_SIZE);
  pos = 0;
  read = source.gcount();
}
