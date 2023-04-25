#include "huffman.h"

#include <cstring>
#include <fstream>
#include <iostream>

int main(int argc, char* argv[]) {
  int mode = 0;
  int input = 0;
  int output = 0;
  if (argc == 2 && std::strcmp(argv[1], "--help") == 0) {
    std::cout << "Usage: [mode] --input [filename] --output [filename]\n\n"
                 "where mode can be:\n"
                 "  --compress\n"
                 "\tprogram compresses input file and writes the result to "
                 "the output file.\n"
                 "  --decompress\n"
                 "\tprogram decompress input file and writes the result to "
                 "the output file.\n";
    return 0;
  }

  if (argc != 6) {
    std::cerr
        << "Error: invalid number of args. For more information use --help.\n";
    return -1;
  }

  for (int i = 0; i < 6; ++i) {
    if (std::strcmp(argv[i], "--compress") == 0) {
      if (mode != 0) {
        std::cerr
            << "Error: incorrect mode. For more information use --help.\n";
        return -1;
      }
      mode = 1;
    } else if (std::strcmp(argv[i], "--decompress") == 0) {
      if (mode != 0) {
        std::cerr
            << "Error: incorrect mode. For more information use --help.\n";
        return -1;
      }
      mode = -1;
    } else if (std::strcmp(argv[i], "--input") == 0) {
      if (input != 0 || i == 5) {
        std::cerr
            << "Error: no input filename. For more information use --help.\n";
        return -1;
      }
      input = ++i;
    } else if (std::strcmp(argv[i], "--output") == 0) {
      if (output != 0 || i == 5) {
        std::cerr
            << "Error: no output filename. For more information use --help.\n";
        return -1;
      }
      output = ++i;
    }
  }

  std::ifstream in;
  std::ofstream out;
  try {
    in.open(argv[input], std::ios::binary);
    if (!in.is_open()) {
      std::cerr << "Error: cannot open input file.\n";
      return -1;
    }
    out.open(argv[output], std::ios::binary);
    if (!out.is_open()) {
      in.close();
      std::cerr << "Error: cannot open output file.\n";
      return -1;
    }
    if (mode == 1) {
      huffman::compress(in, out);
    } else {
      huffman::decompress(in, out);
    }
    in.close();
    out.close();
  } catch (std::invalid_argument& e) {
    in.close();
    out.close();
    std::cerr << e.what() << '\n';
    return -1;
  }
  return 0;
}
