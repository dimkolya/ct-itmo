//
// Created by dimkolya on 02.09.22.
//

#include "huffman.h"
#include "bit_vector.h"
#include "buffered_reader.h"
#include "buffered_writer.h"
#include "huffman_tree.h"

#include <array>
#include <climits>

inline void write_header(buffered_writer& writer,
                         std::array<std::size_t, UCHAR_MAX + 1>& frequency,
                         std::array<bit_vector, UCHAR_MAX + 1>& code_table) {}

void huffman::compress(std::istream& in, std::ostream& out) {
  buffered_reader reader(in);
  std::array<std::size_t, UCHAR_MAX + 1> frequency = { 0 };
  while (!reader.eof()) {
    ++frequency[reader.next()];
  }

  huffman_tree tree(frequency);
  buffered_writer writer(out);
  unsigned char only_char;
  if (tree.empty()) {
    tree.encode_tree(writer, 0);
  } else if (tree.one_char(only_char)) {
    std::size_t file_size = frequency[only_char];
    tree.encode_tree(writer, file_size);
  } else {
    std::array<bit_vector, UCHAR_MAX + 1> code_table = tree.get_code_table();

    std::size_t last_bits = 0;
    for (unsigned char i = 0;; ++i) {
      if (frequency[i] != 0) {
        last_bits +=
            ((frequency[i] % CHAR_BIT) * code_table[i].get_shift()) % CHAR_BIT;
        last_bits %= CHAR_BIT;
      }
      if (i == UCHAR_MAX) {
        break;
      }
    }
    tree.encode_tree(writer, last_bits);
    reader.reset();
    while (!reader.eof()) {
      writer.write(code_table[reader.next()]);
    }
  }
}

void huffman::decompress(std::istream& in, std::ostream& out) {
  buffered_reader reader(in);
  if (reader.eof()) {
    throw std::invalid_argument(
        "The file is damaged: compressed file cannot be empty");
  }
  std::size_t last_bits;
  huffman_tree tree(reader, last_bits);
  buffered_writer writer(out);
  unsigned char only_char;
  if (tree.empty()) {
    if (!reader.eof()) {
      throw std::invalid_argument("The file is damaged: invalid data.");
    }
    return;
  } else if (tree.one_char(only_char)) {
    // last_bits are number of only_char
    for (std::size_t i = 0; i < last_bits; ++i) {
      writer.write(only_char);
    }
  } else {
    if (last_bits != 0) {
      while (true) {
        unsigned char c = reader.next();
        if (last_bits == 0 || !reader.eof()) {
          tree.write(writer, c);
        } else {
          tree.write(writer, c, last_bits);
          break;
        }
      }
    } else {
      while (!reader.eof()) {
        tree.write(writer, reader.next());
      }
    }
  }
}
