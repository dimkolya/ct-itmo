#include "big_integer.h"
#include <algorithm>
#include <cassert>
#include <cstddef>
#include <cstdlib>
#include <cstring>
#include <ostream>
#include <stdexcept>

big_integer::big_integer() : sign(false) {}

big_integer::big_integer(big_integer const& other) = default;

big_integer::big_integer(int a) : big_integer(static_cast<long long>(a)) {}
big_integer::big_integer(unsigned int a)
    : big_integer(static_cast<unsigned long long>(a)) {}
big_integer::big_integer(long a) : big_integer(static_cast<long long>(a)) {}
big_integer::big_integer(unsigned long a)
    : big_integer(static_cast<unsigned long long>(a)) {}
big_integer::big_integer(long long a) : sign(a < 0) {
  unsigned long long b = (a < 0) ? -static_cast<unsigned long long>(a)
                                 : static_cast<unsigned long long>(a);
  while (b != 0) {
    data.push_back(b & MAX_DIGIT);
    b >>= BIT_COUNT;
  }
  remove_leading_zeroes();
}
big_integer::big_integer(unsigned long long a) : sign(false) {
  while (a != 0) {
    data.push_back(a & MAX_DIGIT);
    a >>= BIT_COUNT;
  }
  remove_leading_zeroes();
}

big_integer::big_integer(std::string const& str) : big_integer() {
  if (str.empty() || (str.size() == 1 && str[0] == '-')) {
    throw std::invalid_argument("Invalid number");
  }

  for (size_t i = (str[0] == '-') ? 1 : 0; i < str.size(); i += TEN_EXP) {
    base_t current_ten_pow = 1;
    base_t current_digit = 0;
    for (size_t j = 0; j < TEN_EXP && i + j < str.size(); ++j) {
      if (str[i + j] < '0' || str[i + j] > '9') {
        throw std::invalid_argument("Expected number");
      }
      current_ten_pow *= 10;
      current_digit *= 10;
      current_digit += str[i + j] - '0';
    }
    multiply_by_digit(current_ten_pow);
    *this += current_digit;
  }
  sign = (str[0] == '-');
  remove_leading_zeroes();
}

big_integer::~big_integer() = default;

big_integer& big_integer::operator=(big_integer const& other) = default;

bool big_integer::is_zero() const {
  return data.empty();
}
bool big_integer::is_abs_one() const {
  return data.size() == 1 && data[0] == 1;
}

void big_integer::make_zero() {
  data.clear();
}
void big_integer::remove_leading_zeroes() {
  while (!data.empty() && data.back() == 0) {
    data.pop_back();
  }
}
void big_integer::to_two_compliment() {
  if (sign) {
    for (size_t i = 0; i < data.size(); ++i) {
      data[i] = ~data[i];
    }
    *this -= 1;
  }
}
void big_integer::from_two_compliment() {
  if (sign) {
    *this += 1;
    for (size_t i = 0; i < data.size(); ++i) {
      data[i] = ~data[i];
    }
  }
}

void big_integer::add_with_shift(size_t pos, big_integer const& rhs) {
  size_t max_size = std::max(data.size(), rhs.data.size() + pos) + 1;
  data.resize(max_size, 0);
  bool carry = false;
  for (size_t i = pos; i < max_size
                       && (i - pos < rhs.data.size() || carry); ++i) {
    base_t a_digit = data[i];
    base_t b_digit = (i - pos < rhs.data.size() ? rhs.data[i - pos] : 0);
    data[i] = a_digit + b_digit + (carry ? 1 : 0);
    carry = (a_digit == MAX_DIGIT && (carry || b_digit > 0)) ||
            (MAX_DIGIT - a_digit - (carry ? 1 : 0) < b_digit);
  }
  remove_leading_zeroes();
}
void big_integer::sub_with_shift(size_t pos, const big_integer& b) {
  int a_larger = compare_abs(pos, b);
  if (a_larger == 0) {
    make_zero();
  } else {
    sign ^= (a_larger == -1);
    bool carry = false;
    if (a_larger == 1) {
      for (size_t i = pos;
           i < data.size() && (i - pos < b.data.size() || carry); ++i) {
        base_t this_digit = data[i];
        base_t rhs_digit = (i - pos < b.data.size() ? b.data[i - pos] : 0);
        data[i] = this_digit - rhs_digit - (carry ? 1 : 0);
        carry = (this_digit == 0 && (carry || rhs_digit > 0)) ||
                (rhs_digit > this_digit - (carry ? 1 : 0));
      }
    } else {
      data.resize(b.data.size() + pos, 0);
      for (size_t i = 0; i < pos; ++i) {
        data[i] = -data[i];
        carry = (data[i] != 0);
      }
      for (size_t i = pos; i < data.size(); ++i) {
        base_t this_digit = data[i];
        base_t rhs_digit = b.data[i - pos];
        data[i] = rhs_digit - this_digit - (carry ? 1 : 0);
        carry = (rhs_digit == 0 && (carry || this_digit > 0)) ||
                (this_digit > rhs_digit - (carry ? 1 : 0));
      }
    }
    remove_leading_zeroes();
  }
}

big_integer& big_integer::common_add_sub(bool subtract,
                                         const big_integer& rhs) {
  if (!rhs.is_zero()) {
    if (subtract ^ sign ^ rhs.sign) {
      sub_with_shift(0, rhs);
    } else {
      add_with_shift(0, rhs);
    }
  }
  return *this;
}

big_integer& big_integer::operator+=(big_integer const& rhs) {
  return common_add_sub(false, rhs);
}

big_integer& big_integer::operator-=(big_integer const& rhs) {
  return common_add_sub(true, rhs);
}

void big_integer::multiply_by_digit(base_t rhs) {
  if (!is_zero() && rhs != 1) {
    if (rhs == 0) {
      data.clear();
      return;
    } else {
      base_t carry = 0;
      for (size_t i = 0; i < data.size(); ++i) {
        auto temp = static_cast<base2_t>(data[i]);
        temp = temp * rhs + carry;
        data[i] = temp;
        carry = static_cast<base_t>(temp >> BIT_COUNT);
      }
      if (carry != 0) {
        data.push_back(carry);
      }
    }
  }
}

big_integer& big_integer::operator*=(big_integer const& rhs) {
  if (is_zero()) {
    return *this;
  }
  if (rhs.is_zero()) {
    make_zero();
    return *this;
  }
  if (is_abs_one()) {
    bool temp = sign;
    *this = rhs;
    sign ^= temp;
  } else {
    sign ^= rhs.sign;
    big_integer this_copy(*this);
    multiply_by_digit(rhs.data[0]);
    data.reserve(this_copy.data.size() + rhs.data.size());
    for (size_t i = 1; i < rhs.data.size(); ++i) {
      big_integer temp_copy(this_copy);
      temp_copy.multiply_by_digit(rhs.data[i]);
      add_with_shift(i, temp_copy);
    }
    remove_leading_zeroes();
  }
  return *this;
}

void big_integer::divide_by_digit(base_t rhs, base_t& remainder) {
  if (is_zero() || rhs == 1) {
    return;
  }
  base2_t carry = 0;
  for (size_t i = data.size(); i > 0;) {
    --i;
    base2_t current = data[i] + (carry << BIT_COUNT);
    data[i] = current / rhs;
    carry = current % rhs;
  }
  remove_leading_zeroes();
  remainder = carry;
}

void big_integer::common_div_mod(bool mod, big_integer const& rhs) {
  if (rhs.is_zero()) {
    throw std::runtime_error("Division by zero exception");
  } else if (rhs.data.size() > data.size()) {
    if (!mod) {
      make_zero();
    }
    return;
  }
  if (rhs.data.size() == 1) {
    base_t remainder = 0;
    divide_by_digit(rhs.data[0], remainder);
    if (mod) {
      data.clear();
      if (remainder != 0) {
        data.push_back(remainder);
      }
    } else {
      sign ^= rhs.sign;
    }
  } else {
    base_t f = (1ULL << BIT_COUNT) / (rhs.data.back() + 1);
    big_integer r(*this);
    r.multiply_by_digit(f);
    r.sign = false;

    big_integer d(rhs);
    d.multiply_by_digit(f);
    d.sign = false;

    size_t n = r.data.size();
    size_t m = d.data.size();
    data.clear();
    data.resize(n - m + 1, 0);
    big_integer dq;
    for (size_t k = n - m + 1; k > 0;) {
      --k;
      base_t qt = 0;
      if (k + m < r.data.size()) {
        qt = std::min(((static_cast<base2_t>(r.data[k + m]) << BIT_COUNT) +
                       r.data[k + m - 1]) /
                          d.data.back(),
                      static_cast<base2_t>(MAX_DIGIT));
      } else if (k + m - 1 < r.data.size()) {
        qt = r.data[k + m - 1] / d.data.back();
      } else {
        data[k] = 0;
        continue;
      }
      dq = d;
      dq.multiply_by_digit(qt);
      while (r.compare_abs(k, dq) == -1) {
        --qt;
        dq -= d;
      }
      data[k] = qt;
      r.sub_with_shift(k, dq);
    }
    if (mod) {
      std::swap(data, r.data);
      base_t zero = 0;
      divide_by_digit(f, zero);
    } else {
      sign ^= rhs.sign;
      remove_leading_zeroes();
    }
  }
}

big_integer& big_integer::operator/=(big_integer const& rhs) {
  common_div_mod(false, rhs);
  return *this;
}

big_integer& big_integer::operator%=(big_integer const& rhs) {
  common_div_mod(true, rhs);
  return *this;
}

big_integer& big_integer::operator&=(big_integer const& rhs) {
  to_two_compliment();
  data.resize(rhs.data.size(), sign ? MAX_DIGIT : 0);
  if (rhs.sign) {
    big_integer b(rhs);
    b.to_two_compliment();
    for (size_t i = 0; i < b.data.size(); ++i) {
      data[i] &= b.data[i];
    }
  } else {
    for (size_t i = 0; i < data.size(); ++i) {
      data[i] &= (i < rhs.data.size() ? rhs.data[i] : 0);
    }
  }
  sign &= rhs.sign;
  from_two_compliment();
  remove_leading_zeroes();
  return *this;
}

big_integer& big_integer::operator|=(big_integer const& rhs) {
  to_two_compliment();
  data.resize(rhs.data.size(), sign ? MAX_DIGIT : 0);
  if (rhs.sign) {
    big_integer b(rhs);
    b.to_two_compliment();
    for (size_t i = 0; i < b.data.size(); ++i) {
      data[i] |= b.data[i];
    }
  } else {
    for (size_t i = 0; i < data.size(); ++i) {
      data[i] |= (i < rhs.data.size() ? rhs.data[i] : 0);
    }
  }
  sign |= rhs.sign;
  from_two_compliment();
  remove_leading_zeroes();
  return *this;
}

big_integer& big_integer::operator^=(big_integer const& rhs) {
  to_two_compliment();
  data.resize(rhs.data.size(), sign ? MAX_DIGIT : 0);
  if (rhs.sign) {
    big_integer b(rhs);
    b.data.resize(data.size(), 0);
    b.to_two_compliment();
    for (size_t i = 0; i < b.data.size(); ++i) {
      data[i] ^= b.data[i];
    }
  } else {
    for (size_t i = 0; i < data.size(); ++i) {
      data[i] ^= (i < rhs.data.size() ? rhs.data[i] : 0);
    }
  }
  sign ^= rhs.sign;
  from_two_compliment();
  remove_leading_zeroes();
  return *this;
}

big_integer& big_integer::operator<<=(int rhs) {
  if (rhs > 0) {
    size_t big = rhs / BIT_COUNT;
    size_t low = rhs % BIT_COUNT;
    data.resize(data.size() + big + 1, 0);
    for (size_t i = data.size() - big; i > 1;) {
      --i;
      data[i + big] = (data[i] << low) | (data[i - 1] >> (BIT_COUNT - low));
    }
    data[big] = data[0] << low;
    for (size_t i = 0; i < big; ++i) {
      data[i] = 0;
    }
    remove_leading_zeroes();
  }
  return *this;
}

big_integer& big_integer::operator>>=(int rhs) {
  if (rhs > 0) {
    size_t big = rhs / BIT_COUNT;
    size_t low = rhs % BIT_COUNT;
    if (big >= data.size()) {
      data.clear();
      if (sign) {
        data.push_back(1);
      }
    } else {
      size_t new_size = data.size() - big;
      for (size_t i = big; i < data.size() - 1; ++i) {
        data[i - big] = (data[i] >> low) | (data[i + 1] << (BIT_COUNT - low));
      }
      data[new_size - 1] = data.back() >> low;
      while (data.size() > new_size) {
        data.pop_back();
      }
      remove_leading_zeroes();
      if (sign) {
        --(*this);
      }
    }
  }
  return *this;
}

big_integer big_integer::operator+() const {
  return *this;
}

big_integer big_integer::operator-() const {
  big_integer result(*this);
  result.sign ^= true;
  return result;
}

big_integer big_integer::operator~() const {
  return --(-(*this));
}

big_integer& big_integer::operator++() {
  return *this += 1;
}

big_integer big_integer::operator++(int) {
  big_integer temp(*this);
  ++(*this);
  return temp;
}

big_integer& big_integer::operator--() {
  return *this -= 1;
}

big_integer big_integer::operator--(int) {
  big_integer temp(*this);
  --(*this);
  return temp;
}

big_integer operator+(big_integer a, big_integer const& b) {
  return a += b;
}

big_integer operator-(big_integer a, big_integer const& b) {
  return a -= b;
}

big_integer operator*(big_integer a, big_integer const& b) {
  return a *= b;
}

big_integer operator/(big_integer a, big_integer const& b) {
  return a /= b;
}

big_integer operator%(big_integer a, big_integer const& b) {
  return a %= b;
}

big_integer operator&(big_integer a, big_integer const& b) {
  return a &= b;
}

big_integer operator|(big_integer a, big_integer const& b) {
  return a |= b;
}

big_integer operator^(big_integer a, big_integer const& b) {
  return a ^= b;
}

big_integer operator<<(big_integer a, int b) {
  return a <<= b;
}

big_integer operator>>(big_integer a, int b) {
  return a >>= b;
}

bool operator==(big_integer const& a, big_integer const& b) {
  return (a.is_zero() && b.is_zero()) || (a.sign == b.sign && a.data == b.data);
}

bool operator!=(big_integer const& a, big_integer const& b) {
  return !(a == b);
}

// returns 1 if abs(*this) is greater than abs(B) * BASE ^ pos
//         0 if abs(*this) equals abs(B) * BASE ^ pos
//        -1 if abs(*this) is smaller than abs(B) * BASE ^ pos
int big_integer::compare_abs(size_t pos, const big_integer& b) const {
  if (data.size() != b.data.size() + pos) {
    return (data.size() > b.data.size() + pos) ? 1 : -1;
  }
  for (size_t i = data.size(); i > pos;) {
    --i;
    if (data[i] != b.data[i - pos]) {
      return (data[i] > b.data[i - pos]) ? 1 : -1;
    }
  }
  for (size_t i = 0; i < pos; ++i) {
    if (data[i] != 0) {
      return 1;
    }
  }
  return 0;
}

bool operator<(big_integer const& a, big_integer const& b) {
  return b > a;
}

bool operator>(big_integer const& a, big_integer const& b) {
  if (a.sign != b.sign) {
    return !a.sign;
  } else {
    return a.sign ^ (a.compare_abs(0, b) == 1);
  }
}

bool operator<=(big_integer const& a, big_integer const& b) {
  return !(a > b);
}

bool operator>=(big_integer const& a, big_integer const& b) {
  return !(b > a);
}

std::string to_string(big_integer const& a) {
  if (a.is_zero()) {
    return "0";
  }
  std::string result;
  big_integer copy = a;
  while (true) {
    base_t remainder = 0;
    copy.divide_by_digit(TEN_POW, remainder);
    if (copy.is_zero()) {
      while (remainder != 0) {
        result.push_back((remainder % 10) + '0');
        remainder /= 10;
      }
      break;
    } else {
      for (size_t i = 0; i < TEN_EXP; ++i) {
        result.push_back((remainder % 10) + '0');
        remainder /= 10;
      }
    }
  }
  if (a.sign) {
    result.push_back('-');
  }
  std::reverse(result.begin(), result.end());
  return result;
}

std::ostream& operator<<(std::ostream& s, big_integer const& a) {
  return s << to_string(a);
}
