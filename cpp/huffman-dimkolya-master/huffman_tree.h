//
// Created by dimkolya on 13.09.22.
//

#ifndef UNTITLED1_HUFFMAN_TREE_H
#define UNTITLED1_HUFFMAN_TREE_H

#include "bit_vector.h"
#include "buffered_reader.h"
#include "buffered_writer.h"

#include <array>
#include <climits>

struct huffman_tree {
  friend struct cst_huffman_tree;

  huffman_tree();
  explicit huffman_tree(
      const std::array<std::size_t, UCHAR_MAX + 1>& frequency);
  huffman_tree(buffered_reader& reader, std::size_t& useful_info);
  ~huffman_tree();
  std::array<bit_vector, UCHAR_MAX + 1> get_code_table() const;
  void encode_tree(buffered_writer& writer, std::size_t useful_info) const;
  bool empty() const;
  bool one_char(unsigned char& c) const;
  void write(buffered_writer& writer, unsigned char c);
  void write(buffered_writer& writer, unsigned char c, std::size_t last_bits);

private:
  struct node {
    unsigned char c;
    std::size_t frequency;
    node* zero;
    node* one;
    // it's array pointers because didn't need for compressing
    // CHAR_BIT + 1 for information about size on last cell
    std::array<std::array<unsigned char, CHAR_BIT + 1>, UCHAR_MAX + 1>*
        buffered_chars;
    std::array<node*, UCHAR_MAX + 1>* buffered_way;

    node();
    node(unsigned char c, std::size_t frequency);
    node(node* zero, node* one);
    ~node();
    bool is_leaf() const;
  };

  std::size_t zero_count;
  node* root;
  node* current;

  void destruct(node* current) const;
  void build_code_table(node* current, bit_vector& code,
                        std::array<bit_vector, UCHAR_MAX + 1>& table) const;
  void encode(buffered_writer& writer, node* current) const;
  node* decode(buffered_reader& reader, unsigned char& current_char,
               std::size_t& bit_read, std::array<bool, UCHAR_MAX + 1>& used);
  bool next_bit(buffered_reader& reader, unsigned char& current_char,
                std::size_t& bit_read) const;
  unsigned char next_char(buffered_reader& reader, unsigned char& current_char,
                          std::size_t& bit_read) const;
};

#endif // UNTITLED1_HUFFMAN_TREE_H
