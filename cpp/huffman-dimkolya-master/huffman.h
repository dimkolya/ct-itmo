//
// Created by dimkolya on 02.09.22.
//

#ifndef HUFFMAN_HUFFMAN_H
#define HUFFMAN_HUFFMAN_H

#include <iostream>
#include <vector>

namespace huffman {
void compress(std::istream& in, std::ostream& out);
void decompress(std::istream& in, std::ostream& out);
} // namespace huffman

#endif // HUFFMAN_HUFFMAN_H
