//
// Created by dimkolya on 02.09.22.
//

#include "buffered_writer.h"

#include <climits>

buffered_writer::buffered_writer(std::ostream& source)
    : source(source), written(0), written_bits(0) {}

buffered_writer::~buffered_writer() {
  source.write(reinterpret_cast<char*>(buffer),
               written + (written_bits != 0 ? 1 : 0));
}

void buffered_writer::write(unsigned char c) {
  if (written_bits == 0) {
    write_zero_shift(c);
  } else {
    buffer[written] |= (c >> written_bits);
    char_filled();
    buffer[written] = (c << (CHAR_BIT - written_bits));
  }
}

void buffered_writer::write(const bit_vector& code) {
  if (written_bits == 0) {
    for (std::size_t i = 0; i < code.full_chars; ++i) {
      write_zero_shift(code.data[i]);
    }
    if (code.shift != 0) {
      buffer[written] = code.data[code.full_chars];
      written_bits = code.shift;
    }
  } else if (code.shift + written_bits <= CHAR_BIT) {
    if (code.full_chars != 0) {
      buffer[written] |= (code.data[0] >> written_bits);
      char_filled();
      for (std::size_t i = 0; i < code.full_chars - 1; ++i) {
        write_zero_shift((code.data[i] << (CHAR_BIT - written_bits)) |
                         (code.data[i + 1] >> written_bits));
      }
      buffer[written] =
          (code.data[code.full_chars - 1] << (CHAR_BIT - written_bits));
      if (code.shift != 0) {
        buffer[written] |= (code.data[code.full_chars] >> written_bits);
      }
    } else {
      buffer[written] |= (code.data[0] >> written_bits);
    }
    written_bits += code.shift;
    if (written_bits == CHAR_BIT) {
      written_bits = 0;
      char_filled();
    }
  } else {
    buffer[written] |= (code.data[0] >> written_bits);
    char_filled();
    if (code.full_chars != 0) {
      for (std::size_t i = 0; i < code.full_chars; ++i) {
        write_zero_shift((code.data[i] << (CHAR_BIT - written_bits)) |
                         (code.data[i + 1] >> written_bits));
      }
    }
    buffer[written] = (code.data[code.full_chars] << (CHAR_BIT - written_bits));
    written_bits = written_bits + code.shift - CHAR_BIT;
  }
}

void buffered_writer::write(bool bit) {
  if (written_bits == 0) {
    buffer[written] = ((bit ? 1 : 0) << (CHAR_BIT - 1 - written_bits));
  } else {
    buffer[written] |= ((bit ? 1 : 0) << (CHAR_BIT - 1 - written_bits));
  }
  if (++written_bits == CHAR_BIT) {
    written_bits = 0;
    char_filled();
  }
}

void buffered_writer::write_zero_shift(unsigned char c) {
  buffer[written] = c;
  if (++written == BUFFER_SIZE) {
    source.write(reinterpret_cast<char*>(buffer), BUFFER_SIZE);
    written = 0;
  }
}

void buffered_writer::char_filled() {
  if (++written == BUFFER_SIZE) {
    source.write(reinterpret_cast<char*>(buffer), BUFFER_SIZE);
    written = 0;
  }
}
