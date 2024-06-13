//
// Created by dimkolya on 19.01.23.
//

#include <iostream>
#include <cstring>
#include <fstream>
#include <vector>
#include <algorithm>
#include <set>

int is_pe(std::ifstream &pe) {
    pe.ignore(0x3C);
    uint32_t pos;
    pe.read(reinterpret_cast<char *>(&pos), 4);
    if (pos < 0x3C + 4) {
        std::cout << "Not PE\n";
        return 1;
    }
    pe.ignore(pos - 0x3C - 4);
    char sign[4];
    pe.read(sign, 4);
    if (pe.gcount() != 4) {
        std::cout << "Not PE\n";
        return 1;
    }
    if (sign[0] == 'P' && sign[1] == 'E' && sign[2] == '\0' && sign[3] == '\0') {
        std::cout << "PE\n";
        return 0;
    }
    std::cout << "Not PE\n";
    return 1;
}

int import_functions(std::ifstream &pe) {
    pe.ignore(0x3C);
    uint32_t pos;
    pe.read(reinterpret_cast<char *>(&pos), 4);
    pe.ignore(pos - 0x3C - 4);
    // ignore signature
    pe.ignore(4);
    // ignore COFF
    pe.ignore(20);

    // goto rva and size of import table
    pe.ignore(0x78);
    uint32_t import_table_rva;
    uint32_t import_table_size;
    pe.read(reinterpret_cast<char *>(&import_table_rva), 4);
    pe.read(reinterpret_cast<char *>(&import_table_size), 4);
    // ignore optional header
    pe.ignore(240 - 0x78 - 4 - 4);

    uint32_t import_raw;
    // headers
    uint32_t headers_count = 0;
    while (true) {
        ++headers_count;
        // goto section virtual size and section rva
        pe.ignore(0x8);
        uint32_t section_virtual_size;
        uint32_t section_rva;
        pe.read(reinterpret_cast<char *>(&section_virtual_size), 4);
        pe.read(reinterpret_cast<char *>(&section_rva), 4);
        if (section_rva <= import_table_rva && import_table_rva < section_rva + section_virtual_size) {
            // goto section raw
            pe.ignore(4);
            uint32_t section_raw;
            pe.read(reinterpret_cast<char *>(&section_raw), 4);
            import_raw = section_raw + import_table_rva - section_rva;
            pe.ignore(40 - 0x8 - 4 - 4 - 4 - 4);
            break;
        }
        // goto next header
        pe.ignore(40 - 0x8 - 4 - 4);
    }
    // goto import raw
    pe.clear();
    pe.seekg(0, std::ios::beg);
    pe.ignore(import_raw);

    std::vector <std::pair<uint32_t, uint32_t>> ilt_rvas;
    std::vector <uint32_t> lib_name_rvas;
    while (true) {
        uint32_t ilt_rva;
        uint32_t lib_name_rva;
        char temp[12];
        pe.read(reinterpret_cast<char *>(&ilt_rva), 4);
        pe.read(temp, 8);
        pe.read(reinterpret_cast<char *>(&lib_name_rva), 4);
        pe.read(temp + 8, 4);
        if (ilt_rva != 0 || lib_name_rva != 0 ||
            std::any_of(temp, temp + 12, [](char c) { return c != '\0'; })) {
            ilt_rvas.push_back(std::make_pair(ilt_rva, lib_name_rvas.size()));
            lib_name_rvas.push_back(lib_name_rva);
        } else {
            break;
        }
    }
    std::sort(ilt_rvas.begin(), ilt_rvas.end());

    if (!lib_name_rvas.empty()) {
        std::vector <std::pair<uint32_t, uint32_t>> function_rvas;
        uint32_t ilt_first_raw = ilt_rvas.front().first + import_raw - import_table_rva;
        pe.clear();
        pe.seekg(0, std::ios::beg);
        pe.ignore(ilt_first_raw);
        uint32_t read_lookup_chunks = 0;
        for (std::size_t i = 0;; ++i) {
            uint32_t temp_read_lookup_chunks = 0;
            while (true) {
                uint64_t function_name_rva;
                pe.read(reinterpret_cast<char *>(&function_name_rva), 8);
                ++read_lookup_chunks;
                ++temp_read_lookup_chunks;
                if (function_name_rva == 0) break;
                if ((function_name_rva & 0x8000000000000000) == 0) {
                    function_rvas.push_back(std::make_pair(function_name_rva, ilt_rvas[i].second));
                }
            }
            if (i < ilt_rvas.size() - 1) {
                pe.ignore(ilt_rvas[i + 1].first - ilt_rvas[i].first - 8 * temp_read_lookup_chunks);
            } else {
                break;
            }
        }
        std::sort(function_rvas.begin(), function_rvas.end());
        uint32_t first_raw;
        uint32_t read_chars = 0;
        uint32_t pre_rva;
        auto function_cur_it = function_rvas.begin();
        auto lib_cur_it = lib_name_rvas.begin();
        std::vector < std::pair < std::string, std::vector < std::string>>> result(lib_name_rvas.size());
        pe.clear();
        pe.seekg(0, std::ios::beg);
        if (function_cur_it->first < *lib_cur_it) {
            first_raw = function_cur_it->first + import_raw - import_table_rva;
            pe.ignore(first_raw);
            pre_rva = function_cur_it->first;

            pe.ignore(2);
            result[function_cur_it->second].second.emplace_back();
            read_chars += 2;
            char c;
            while (true) {
                pe >> c;
                ++read_chars;
                if (c == '\0') break;
                result[function_cur_it->second].second.back().push_back(c);
            }
            ++function_cur_it;
        } else {
            first_raw = *lib_cur_it + import_raw - import_table_rva;
            pe.ignore(first_raw);
            pre_rva = *lib_cur_it;

            char c;
            while (true) {
                pe >> c;
                ++read_chars;
                if (c == '\0') break;
                result[lib_cur_it - lib_name_rvas.begin()].first.push_back(c);
            }
            ++lib_cur_it;
        }
        while (function_cur_it != function_rvas.end() || lib_cur_it != lib_name_rvas.end()) {
            if (function_cur_it != function_rvas.end() &&
                (lib_cur_it == lib_name_rvas.end() || function_cur_it->first < *lib_cur_it)) {
                pe.ignore(function_cur_it->first - pre_rva - read_chars);
                read_chars = 2;
                pre_rva = function_cur_it->first;
                pe.ignore(2);
                result[function_cur_it->second].second.emplace_back();
                char c;
                while (true) {
                    pe >> c;
                    ++read_chars;
                    if (c == '\0') break;
                    result[function_cur_it->second].second.back().push_back(c);
                }
                ++function_cur_it;
            } else {
                pe.ignore(*lib_cur_it - pre_rva - read_chars);
                read_chars = 0;
                pre_rva = *lib_cur_it;
                char c;
                while (true) {
                    pe >> c;
                    ++read_chars;
                    if (c == '\0') break;
                    result[lib_cur_it - lib_name_rvas.begin()].first.push_back(c);
                }
                ++lib_cur_it;
            }
        }
        for (auto lib: result) {
            std::cout << lib.first << '\n';
            for (std::string &temp: lib.second) {
                std::cout << "    " << temp << '\n';
            }
        }
    }
    return 0;
}

int main(int argc, char **argv) {
    if (argc != 3) {
        std::cerr << "Please, enter 2 arguments: operation and filename.\n";
        return -1;
    }
    std::ifstream pe;
    pe.open(argv[2]);
    if (std::strcmp(argv[1], "is-pe") == 0) {
        return is_pe(pe);
    } else if (std::strcmp(argv[1], "import-functions") == 0) {
        return import_functions(pe);
    }
    std::cout << "Unknown operation\n";
    return 0;
}