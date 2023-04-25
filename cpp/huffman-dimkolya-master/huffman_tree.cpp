//
// Created by dimkolya on 13.09.22.
//

#include "huffman_tree.h"

#include <algorithm>
#include <array>

huffman_tree::node::node()
    : c('\0'), frequency('\0'), zero(nullptr), one(nullptr),
      buffered_chars(nullptr), buffered_way(nullptr) {}

huffman_tree::node::node(unsigned char c, std::size_t frequency)
    : c(c), frequency(frequency), zero(nullptr), one(nullptr),
      buffered_chars(nullptr), buffered_way(nullptr) {}

huffman_tree::node::node(huffman_tree::node* zero, huffman_tree::node* one)
    : c('\0'), frequency(zero->frequency + one->frequency), zero(zero),
      one(one), buffered_chars(nullptr), buffered_way(nullptr) {}

huffman_tree::node::~node() {
  delete (buffered_chars);
  delete (buffered_way);
}

bool huffman_tree::node::is_leaf() const {
  return zero == nullptr && one == nullptr;
}

huffman_tree::huffman_tree() : zero_count(0), root(nullptr), current(nullptr) {}

huffman_tree::huffman_tree(
    const std::array<std::size_t, UCHAR_MAX + 1>& frequency)
    : huffman_tree() {
  std::array<node*, UCHAR_MAX + 1> arr = {nullptr};
  for (unsigned char i = 0;; ++i) {
    if (frequency[i] != 0) {
      arr[zero_count++] = new node(i, frequency[i]);
    }
    if (i == UCHAR_MAX) {
      break;
    }
  }
  if (zero_count == 0) {
    return;
  } else {
    std::sort(
        std::begin(arr), std::begin(arr) + zero_count,
        [](node* zero, node* one) { return zero->frequency > one->frequency; });
    for (std::size_t i = zero_count - 1; i != 0; --i) {
      node* temp = new node(arr[i], arr[i - 1]);
      arr[i - 1] = temp;
      std::size_t j = i - 1;
      while (j != 0 && temp->frequency > arr[j - 1]->frequency) {
        std::swap(arr[j], arr[j - 1]);
        --j;
      }
    }
    root = arr[0];
    current = arr[0];
    --zero_count;
  }
}

huffman_tree::huffman_tree(buffered_reader& reader, std::size_t& useful_info)
    : huffman_tree() {
  unsigned char current_char = reader.next();
  std::size_t bit_read = 0;
  if (next_bit(reader, current_char, bit_read)) {
    std::array<bool, UCHAR_MAX + 1> used = {false};
    root = decode(reader, current_char, bit_read, used);
    current = root;
  }
  useful_info = 0;
  std::size_t two_pow = 1;
  if (root == nullptr) {
    return;
  } else if (root->is_leaf()) {
    std::size_t max_bits = sizeof(std::size_t) * CHAR_BIT;
    while (bit_read != CHAR_BIT || !reader.eof()) {
      bool bit = next_bit(reader, current_char, bit_read);
      if (max_bits == 0 && bit) {
        throw std::invalid_argument("File is damaged: cannot decode header.");
      } else if (max_bits != 0) {
        --max_bits;
      }
      if (bit) {
        useful_info += two_pow;
      }
      two_pow *= 2;
    }
  } else {
    std::size_t max_bits = 0;
    while (two_pow <= CHAR_BIT) {
      two_pow *= 2;
      ++max_bits;
    }
    two_pow = 1;
    for (std::size_t i = 0; i < max_bits; ++i) {
      if (next_bit(reader, current_char, bit_read)) {
        useful_info += two_pow;
      }
      two_pow *= 2;
    }
    if (useful_info >= CHAR_BIT) {
      throw std::invalid_argument(
          "The file is damaged: invalid number of last bits");
    }
  }
}

huffman_tree::~huffman_tree() {
  if (root != nullptr) {
    destruct(root);
  }
}

std::array<bit_vector, UCHAR_MAX + 1> huffman_tree::get_code_table() const {
  bit_vector temp;
  std::array<bit_vector, UCHAR_MAX + 1> ans;
  if (root != nullptr) {
    build_code_table(root, temp, ans);
  }
  return ans;
}

void huffman_tree::encode_tree(buffered_writer& writer,
                               std::size_t useful_info) const {
  if (root != nullptr) {
    // crutch for empty file
    writer.write(true);
    encode(writer, root);
    if (!root->is_leaf()) {
      useful_info %= CHAR_BIT;
      std::size_t max_bits = 0;
      std::size_t two_pow = 1;
      while (two_pow <= CHAR_BIT) {
        ++max_bits;
        two_pow *= 2;
      }
      two_pow = 1;
      for (std::size_t i = 0; i < max_bits; ++i) {
        if (useful_info % 2 == 1) {
          writer.write(true);
        } else {
          writer.write(false);
        }
        useful_info /= 2;
      }
      // fill to the full char
      std::size_t header_bits = (max_bits + 2 * zero_count + 2) % CHAR_BIT;
      if (header_bits == 0) {
        return;
      }
      for (std::size_t i = 0; i < CHAR_BIT - header_bits; ++i) {
        writer.write(false);
      }
    } else {
      while (useful_info != 0) {
        if (useful_info % 2 == 1) {
          writer.write(true);
        } else {
          writer.write(false);
        }
        useful_info /= 2;
      }
    }
  } else {
    writer.write(false);
  }
}

bool huffman_tree::empty() const {
  return root == nullptr;
}

bool huffman_tree::one_char(unsigned char& c) const {
  if (!empty()) {
    c = root->c;
  }
  return !empty() && root->is_leaf();
}

void huffman_tree::write(buffered_writer& writer, unsigned char c) {
  if (current->buffered_way == nullptr) {
    current->buffered_chars =
        new std::array<std::array<unsigned char, CHAR_BIT + 1>, UCHAR_MAX + 1>();
    current->buffered_way = new std::array<node*, UCHAR_MAX + 1>();
    for (unsigned char i = 0;; ++i) {
      // at the beginning char seq size is 0
      (*(current->buffered_chars))[i][CHAR_BIT] = 0;

      node* temp = current;
      for (std::size_t j = 0; j < CHAR_BIT; ++j) {
        if ((i & (1 << (CHAR_BIT - 1 - j))) == 0) {
          temp = temp->zero;
          if (temp->is_leaf()) {
            (*(current->buffered_chars))[i][
                (*(current->buffered_chars))[i][CHAR_BIT]++] = temp->c;
            temp = root;
          }
        } else {
          temp = temp->one;
          if (temp->is_leaf()) {
            (*(current->buffered_chars))[i][
                (*(current->buffered_chars))[i][CHAR_BIT]++] = temp->c;
            temp = root;
          }
        }
      }
      (*(current->buffered_way))[i] = temp;

      if (i == UCHAR_MAX) {
        break;
      }
    }
  }
  for (std::size_t i = 0; i < (*(current->buffered_chars))[c][CHAR_BIT]; ++i) {
    writer.write((*(current->buffered_chars))[c][i]);
  }
  current = (*(current->buffered_way))[c];
}

void huffman_tree::write(buffered_writer& writer, unsigned char c,
                         std::size_t last_bits) {
  for (std::size_t i = 0; i < last_bits; ++i) {
    if ((c & (1 << (CHAR_BIT - 1 - i))) == 0) {
      current = current->zero;
      if (current->is_leaf()) {
        writer.write(current->c);
        current = root;
      }
    } else {
      current = current->one;
      if (current->is_leaf()) {
        writer.write(current->c);
        current = root;
      }
    }
  }
  if (current != root) {
    throw std::invalid_argument("The file is damaged: invalid data.");
  }
}

void huffman_tree::destruct(node* current) const {
  if (current->zero != nullptr) {
    destruct(current->zero);
  }
  if (current->one != nullptr) {
    destruct(current->one);
  }
  delete (current);
}

void huffman_tree::build_code_table(
    node* current, bit_vector& code,
    std::array<bit_vector, UCHAR_MAX + 1>& table) const {
  if (current->is_leaf()){
    table[static_cast<unsigned char>(current->c)] = code;
  } else {
    code.push_back(false);
    build_code_table(current->zero, code, table);
    code.pop_back();
    code.push_back(true);
    build_code_table(current->one, code, table);
    code.pop_back();
  }
}

void huffman_tree::encode(buffered_writer& writer, node* current) const {
  if (current->zero != nullptr) {
    writer.write(false);
    encode(writer, current->zero);
    encode(writer, current->one);
  } else {
    writer.write(true);
    writer.write(current->c);
  }
}

huffman_tree::node*
huffman_tree::decode(buffered_reader& reader, unsigned char& current_char,
                     std::size_t& bit_read,
                     std::array<bool, UCHAR_MAX + 1>& used) {
  if (next_bit(reader, current_char, bit_read)) {
    unsigned char c = next_char(reader, current_char, bit_read);
    if (used[c]) {
      throw std::invalid_argument(
          "The file is damaged: cannot decode huffman tree.");
    } else {
      used[c] = true;
    }
    return new node(c, 0);
  } else {
    if (zero_count == UCHAR_MAX) {
      throw std::invalid_argument(
          "The file is damaged: cannot decode huffman tree.");
    }
    ++zero_count;
    node* zero = decode(reader, current_char, bit_read, used);
    node* one = decode(reader, current_char, bit_read, used);
    return new node(zero, one);
  }
}

bool huffman_tree::next_bit(buffered_reader& reader,
                            unsigned char& current_char,
                            size_t& bit_read) const {
  if (bit_read == CHAR_BIT) {
    if (reader.eof()) {
      throw std::invalid_argument(
          "The file is damaged: cannot decode file header.");
    }
    current_char = reader.next();
    bit_read = 0;
  }
  ++bit_read;
  return ((current_char & (1 << (CHAR_BIT - bit_read))) != 0);
}

unsigned char huffman_tree::next_char(buffered_reader& reader,
                                      unsigned char& current_char,
                                      size_t& bit_read) const {
  unsigned char ans = 0;
  for (std::size_t i = 0; i < CHAR_BIT; ++i) {
    ans <<= 1;
    ans |= (next_bit(reader, current_char, bit_read) ? 1 : 0);
  }
  return ans;
}
