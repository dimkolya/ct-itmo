#include <cstdio>
#include <cstring>

const std::size_t BUFFER_SIZE = 1024;

int main(int argc, char *argv[]) {
    if (argc != 3) {
        std::perror("Please, enter 2 arguments: input file name and non-empty string.\n");
        return -1;
    }
    FILE *in = std::fopen(argv[1], "r");
    if (!in) {
        std::perror("File opening failed.\n");
        return -1;
    }

    char *str = argv[2];
    std::size_t string_size = std::strlen(str);

    std::size_t kmp_array[4096];
    kmp_array[0] = 0;
    std::size_t str_iterator = 0;
    for (std::size_t i = 1; i < string_size; i++) {
        while (str_iterator > 0 && str[i] != str[str_iterator]) {
            str_iterator = kmp_array[str_iterator - 1];
        }
        if (str[i] == str[str_iterator]) {
            str_iterator++;
        }
        kmp_array[i] = str_iterator;
    }

    bool is_founded = false;
    while (!feof(in)) {
        char buffer[BUFFER_SIZE];
        std::size_t buffer_size = fread(buffer, sizeof(char), BUFFER_SIZE, in);
        if (ferror(in)) {
            perror("File reading error.\n");
            return -1;
        }

        for (std::size_t buffer_iterator = 0; buffer_iterator < buffer_size; buffer_iterator++) {
            while (str_iterator > 0 && str[str_iterator] != buffer[buffer_iterator]) {
                str_iterator = kmp_array[str_iterator - 1];
            }
            if (str[str_iterator] == buffer[buffer_iterator]) {
                str_iterator++;
            }
            if (str_iterator == string_size) {
                is_founded = true;
                break;
            }
        }

        if (is_founded) {
            break;
        }
    }
    std::fclose(in);

    if (is_founded) {
        std::printf("Yes\n");
    } else {
        std::printf("No\n");
    }
    return 0;
}
