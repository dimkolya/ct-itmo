#include "../buffered_reader.h"
#include "../huffman.h"
#include "gtest/gtest.h"

#include <climits>
#include <fstream>
#include <random>
#include <array>

namespace {
void check_compress_decompress(std::string& data) {
  std::stringstream in(data);
  std::ostringstream temp_out;
  huffman::compress(in, temp_out);
  std::istringstream temp_in(temp_out.str());
  std::ostringstream out;
  huffman::decompress(temp_in, out);
  ASSERT_EQ(data, out.str());
}

void check_ratio_random(std::string& data) {
  std::size_t char_count = 0;
  std::array<bool, UCHAR_MAX + 1> temp = { false };
  for (char & i : data) {
    if (!temp[static_cast<unsigned char>(i)]) {
      ++char_count;
      i = true;
    }
  }
  std::stringstream in(data);
  std::ostringstream temp_out;
  huffman::compress(in, temp_out);
  // huffman compressor is bad for random files
  ASSERT_LE(temp_out.str().size(), data.size() + 1 + (char_count * 11) / 8);
}

void check_ratio_good(std::istream& in) {
  std::size_t size = 0;
  while (true) {
    in.get();
    if (in.eof()) {
      break;
    }
    ++size;
  }
  in.clear();
  in.seekg(0, std::ios::beg);
  std::stringstream out;
  huffman::compress(in, out);
  // 75%
  ASSERT_LE(4 * out.str().size(), 3 * size);
}
} // namespace

TEST(correctness, empty) {
  std::string data;
  check_compress_decompress(data);

  // empty compress result must not be empty
  {
    std::stringstream in(data);
    std::ostringstream temp_out;
    huffman::compress(in, temp_out);
    ASSERT_EQ(false, temp_out.str().empty());
  }

  {
    std::stringstream in(data);
    std::ostringstream temp_out;
    EXPECT_THROW(huffman::decompress(in, temp_out), std::invalid_argument);
  }

  check_ratio_random(data);
}

TEST(correctness, one_symbol) {
  std::string data;
  for (std::size_t i = 0; i < 1000; ++i) {
    data.push_back('a');
    check_compress_decompress(data);
    check_ratio_random(data);
  }
}

TEST(correctness, difference_symbols) {
  std::string data;
  for (unsigned char i = 0;; ++i) {
    data.push_back(static_cast<char>(i));
    check_compress_decompress(data);
    check_ratio_random(data);
    if (i == UCHAR_MAX) {
      break;
    }
  }
}

TEST(correctness, random_short) {
  for (std::size_t i = 0; i < 100; ++i) {
    std::string data;
    for (std::size_t j = 0; j < 1000; ++j) {
      data.push_back(static_cast<char>(std::rand()));
    }
    check_compress_decompress(data);
    check_ratio_random(data);
  }
}

TEST(correctness, random_long) {
  for (std::size_t i = 0; i < 100; ++i) {
    std::string data;
    for (std::size_t j = 0; j < 100000; ++j) {
      data.push_back(static_cast<char>(std::rand()));
    }
    check_compress_decompress(data);
    check_ratio_random(data);
  }
}

//TEST(real_file, rus_txt) {
//  std::ifstream in("data/rus.txt", std::ios::binary);
//  check_ratio_good(in);
//}
//
//TEST(real_file, eng_txt) {
//  std::ifstream in("data/eng.txt", std::ios::binary);
//  check_ratio_good(in);
//}
//
