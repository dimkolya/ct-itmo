#pragma once

#include <cstdint>
#include <functional>
#include <iosfwd>
#include <string>
#include <vector>
using base_t = uint32_t;
using base2_t = uint64_t;
const base_t MAX_DIGIT = UINT32_MAX;
const size_t BIT_COUNT = 32;
const base_t TEN_POW = 1'000'000'000;
const size_t TEN_EXP = 9;

struct big_integer {
  big_integer();
  big_integer(big_integer const& other);
  big_integer(int a);
  big_integer(unsigned int a);
  big_integer(long a);
  big_integer(unsigned long a);
  big_integer(long long a);
  big_integer(unsigned long long a);
  explicit big_integer(std::string const& str);
  ~big_integer();

  big_integer& operator=(big_integer const& other);

  big_integer& operator+=(big_integer const& rhs);
  big_integer& operator-=(big_integer const& rhs);
  big_integer& operator*=(big_integer const& rhs);
  big_integer& operator/=(big_integer const& rhs);
  big_integer& operator%=(big_integer const& rhs);

  big_integer& operator&=(big_integer const& rhs);
  big_integer& operator|=(big_integer const& rhs);
  big_integer& operator^=(big_integer const& rhs);

  big_integer& operator<<=(int rhs);
  big_integer& operator>>=(int rhs);

  big_integer operator+() const;
  big_integer operator-() const;
  big_integer operator~() const;

  big_integer& operator++();
  big_integer operator++(int);

  big_integer& operator--();
  big_integer operator--(int);

  friend bool operator==(big_integer const& a, big_integer const& b);
  friend bool operator!=(big_integer const& a, big_integer const& b);
  friend bool operator<(big_integer const& a, big_integer const& b);
  friend bool operator>(big_integer const& a, big_integer const& b);
  friend bool operator<=(big_integer const& a, big_integer const& b);
  friend bool operator>=(big_integer const& a, big_integer const& b);

  friend std::string to_string(big_integer const& a);

private:
  bool sign;
  std::vector<base_t> data;

  bool is_zero() const;
  bool is_abs_one() const;

  void make_zero();
  void remove_leading_zeroes();
  void to_two_compliment();
  void from_two_compliment();

  void add_with_shift(size_t pos, big_integer const& rhs);
  void sub_with_shift(size_t pos, big_integer const& b);

  big_integer& common_add_sub(bool subtract, big_integer const &rhs);
  void common_div_mod(bool mod, big_integer const &rhs);

  void multiply_by_digit(base_t rhs);
  void divide_by_digit(base_t rhs, base_t&remainder);

  int compare_abs(size_t pos, big_integer const & rhs) const;
};

big_integer operator+(big_integer a, big_integer const& b);
big_integer operator-(big_integer a, big_integer const& b);
big_integer operator*(big_integer a, big_integer const& b);
big_integer operator/(big_integer a, big_integer const& b);
big_integer operator%(big_integer a, big_integer const& b);

big_integer operator&(big_integer a, big_integer const& b);
big_integer operator|(big_integer a, big_integer const& b);
big_integer operator^(big_integer a, big_integer const& b);

big_integer operator<<(big_integer a, int b);
big_integer operator>>(big_integer a, int b);

bool operator==(big_integer const& a, big_integer const& b);
bool operator!=(big_integer const& a, big_integer const& b);
bool operator<(big_integer const& a, big_integer const& b);
bool operator>(big_integer const& a, big_integer const& b);
bool operator<=(big_integer const& a, big_integer const& b);
bool operator>=(big_integer const& a, big_integer const& b);

std::string to_string(big_integer const& a);
std::ostream& operator<<(std::ostream& s, big_integer const& a);
