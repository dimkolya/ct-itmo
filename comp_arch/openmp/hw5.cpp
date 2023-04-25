#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <chrono>
#ifdef _OPENMP
#include <omp.h>
#endif

unsigned long min3(unsigned long a, unsigned long b, unsigned long c) {
    if (a < b) {
        if (a < c) {
            return a;
        } else {
            return c;
        }
    } else {
        if (b < c) {
            return b;
        } else {
            return c;
        }
    }
}

unsigned long max3(unsigned long a, unsigned long b, unsigned long c) {
    if (a > b) {
        if (a > c) {
            return a;
        } else {
            return c;
        }
    } else {
        if (b > c) {
            return b;
        } else {
            return c;
        }
    }
}

int main(int argc, char* argv[]) {
    if (argc != 5) {
        printf("Please, enter 4 arguments: number of threads, name of input file, name of output file and coefficient");
        return -1;
    }
    char *endptr;
#ifdef _OPENMP
    long threads_num = strtol(argv[1], &endptr, 10);
    if (*endptr != '\0' || threads_num < 0) {
        printf("Number of threads must be a non-negative integer, actual: %s", argv[1]);
        return -1;
    }
    if (threads_num == 0) {
        threads_num = omp_get_thread_num();
    }
    omp_set_num_threads(threads_num);
#else
    printf("OpenMP not included\n");
#endif
    float coef = strtof(argv[4], &endptr);
    if (*endptr != '\0' || coef < 0 || 2 * coef >= 1) {
        printf("Coefficient must be a float number from 0 to 0.5 [0, 0.5), actual: %s", argv[4]);
        return -1;
    }
    FILE *in = fopen(argv[2], "rb");
    if (in == NULL) {
        printf("File cannot be opened");
        return -1;
    }
    FILE *out = fopen(argv[3], "wb");
    if (out == NULL) {
        printf("Cannot open output file");
    }
    char x, y;
    unsigned long width, height, max_value;
    if (fscanf(in, "%c%c", &x, &y) == 2) {
        if (x == 'P' && y == '5') {
            if (fscanf(in, "\n%lu%lu\n%lu\n", &width, &height, &max_value) != 3) {
                printf("Unknown P5 file format");
                fclose(in);
                return -1;
            }
            if (max_value != 255) {
                printf("Unsupported max value: %lu", max_value);
                fclose(in);
                return -1;
            }
            size_t count =  width * height;
            unsigned char *data = (unsigned char *) (malloc(sizeof(unsigned char) * count));
            if (data == NULL) {
                printf("Not enough RAM");
                fclose(in);
                return -1;
            }
            size_t read = fread(data, sizeof(unsigned char), count, in);
            if (read != count || feof(in) || ferror(in)) {
                printf("Image reading error");
                fclose(in);
                free(data);
                return -1;
            }
            fclose(in);
#if __cplusplus >= 201103L || (defined(_MSC_VER) && _MSC_VER >= 1900)
            auto start_t = std::chrono::high_resolution_clock::now();
#endif
            unsigned long shade[256];
            for (size_t i = 0; i < 256; i++) {
                shade[i] = 0;
            }

#pragma omp parallel shared(count, data, shade)
            {
                unsigned long shade_th[256];
                for (size_t i = 0; i < 256; i++) {
                    shade_th[i] = 0;
                }
#pragma omp for schedule(static)
                for (size_t i = 0; i < count; i++) {
                    shade_th[data[i] + 256]++;
                }
#pragma omp critical
                for (size_t i = 0; i < 256; i++) {
                    shade[i] += shade_th[i];
                }
            }

            unsigned char min_col = 0;
            unsigned char max_col = 0;
            for (size_t i = 0; i < 256; i++) {
                if (shade[i] != 0) {
                    min_col = i;
                    break;
                }
            }
            for (size_t i = 255; i >= 0; i++) {
                if (shade[i] != 0) {
                    max_col = i;
                    break;
                }
            }

            if (min_col != max_col) {
                unsigned long ign_cnt = count * coef / 2;
                unsigned long pre_sum = 0;
                unsigned char abs_min;
                for (size_t i = 0; i < 256; i++) {
                    pre_sum += shade[i];
                    if (pre_sum > ign_cnt || i == 255) {
                        abs_min = i;
                        break;
                    } else if (pre_sum == ign_cnt) {
                        abs_min = i + 1;
                        break;
                    }
                }
                unsigned char abs_max;
                pre_sum = 0;
                for (size_t i = 255; i >= 0; i--) {
                    pre_sum += shade[i];
                    if (pre_sum > ign_cnt || i == 0) {
                        abs_min = i;
                        break;
                    } else if (pre_sum == ign_cnt) {
                        abs_min = i + 1;
                        break;
                    }
                }

                if (abs_min == abs_max) {
                    printf("Too large coefficient error");
                    free(data);
                    fclose(out);
                    return -1;
                }

                unsigned long cnst = abs_max - abs_min;
#pragma omp parallel for schedule(static) shared(count, data, cnst, abs_min)
                for (size_t i = 0; i < count; i++) {
                    if (data[i] <= abs_min) {
                        data[i] = 0;
                    } else if (data[i] >= abs_max) {
                        data[i] = 255;
                    } else {
                        data[i] = 255 * (data[i] - abs_min) / cnst;
                    }
                }
            } else {
                printf("Image includes only one color, editing stopped\n");
            }
            
#if __cplusplus >= 201103L || (defined(_MSC_VER) && _MSC_VER >= 1900)
            auto end_t = std::chrono::high_resolution_clock::now();
            std::chrono::duration<double> t = end_t - start_t;
#ifdef _OPENMP
            printf("Time (%i tread(s)): %g ms\n", threads_num, (double)(t.count() * 1000.0));
#endif
#endif
            
            fprintf(out, "P5\n%lu\n%lu\n%lu\n", width, height, max_value);
            fwrite(data, sizeof(unsigned char), count, out);
            free(data);
            fclose(out);
        } else if (x == 'P' && y == '6') {
            if (fscanf(in, "\n%lu%lu\n%lu\n", &width, &height, &max_value) != 3) {
                printf("Unknown P6 file format");
                fclose(in);
                return -1;
            }
            if (max_value != 255) {
                printf("Unsupported max value: %lu", max_value);
                fclose(in);
                return -1;
            }
            size_t count = 3 * width * height;
            unsigned char *data = (unsigned char *) (malloc(sizeof(unsigned char) * count));
            if (data == NULL) {
                printf("Not enough RAM");
                fclose(in);
                return -1;
            }
            size_t read = fread(data, sizeof(unsigned char), count, in);
            if (read != count || feof(in) || ferror(in)) {
                printf("Image reading error");
                fclose(in);
                free(data);
                return -1;
            }
            fclose(in);
#if __cplusplus >= 201103L || (defined(_MSC_VER) && _MSC_VER >= 1900)
            auto start_t = std::chrono::high_resolution_clock::now();
#endif
            unsigned long rgb[256 * 3];
            for (size_t i = 0; i < 256 * 3; i++) {
                rgb[i] = 0;
            }

#pragma omp parallel shared(count, data, rgb)
            {
                unsigned long rgb_th[256 * 3];
                long r = 0;
                for (size_t i = 0; i < 256 * 3; i++) {
                    rgb_th[i] = 0;
                }
#pragma omp for schedule(static)
                for (size_t i = 0; i < count; i++) {
                    rgb_th[data[i] + 256 * r]++;
                    r++;
                    if (r == 3) {
                        r = 0;
                    }
                }
#pragma omp critical
                for (size_t i = 0; i < 256 * 3; i++) {
                    rgb[i] += rgb_th[i];
                }
            }

            unsigned char min_col = 0;
            unsigned char max_col = 0;
            for (size_t i = 0; i < 256 * 3; i++) {
                if (rgb[i] != 0) {
                    min_col = i;
                    break;
                }
            }
            for (size_t i = 256 * 3 - 1; i >= 0; i++) {
                if (rgb[i] != 0) {
                    max_col = i;
                    break;
                }
            }

            if (min_col != max_col) {
                unsigned long ign_cnt = count * coef / 2;
                unsigned long pre_sum;
                unsigned char min_rgb[3];
                for (size_t col = 0; col < 3; col++) {
                    pre_sum = 0;
                    for (size_t i = 0; i < 256; i++) {
                        pre_sum += rgb[i + col * 256];
                        if (pre_sum > ign_cnt || i == 255) {
                            min_rgb[col] = i;
                            break;
                        } else if (pre_sum == ign_cnt) {
                            min_rgb[col] = i + 1;
                            break;
                        }
                    }
                }
                unsigned char max_rgb[3];
                for (size_t col = 0; col < 3; col++) {
                    pre_sum = 0;
                    for (size_t i = 255; i >= 0; i--) {
                        pre_sum += rgb[i + 256 * col];
                        if (pre_sum > ign_cnt || i == 0) {
                            max_rgb[col] = i;
                            break;
                        } else if (pre_sum == ign_cnt) {
                            max_rgb[col] = i - 1;
                            break;
                        }
                    }
                }
                unsigned char abs_min = min3(min_rgb[0], min_rgb[1], min_rgb[2]);
            printf("%d", abs_min);
                unsigned char abs_max = max3(max_rgb[0], max_rgb[1], max_rgb[2]);

                if (abs_min == abs_max) {
                    printf("Too large coefficient error");
                    free(data);
                    fclose(out);
                    return -1;
                }

                unsigned long cnst = abs_max - abs_min;
#pragma omp parallel for schedule(static) shared(count, data, cnst, abs_min)
                for (size_t i = 0; i < count; i++) {
                    if (data[i] <= abs_min) {
                        data[i] = 0;
                    } else if (data[i] >= abs_max) {
                        data[i] = 255;
                    } else {
                        data[i] = 255 * (data[i] - abs_min) / cnst;
                    }
                }
            } else {
                printf("Image includes only one color, editing stopped\n");
            }
#if __cplusplus >= 201103L || (defined(_MSC_VER) && _MSC_VER >= 1900)
            auto end_t = std::chrono::high_resolution_clock::now();
            std::chrono::duration<double> t = end_t - start_t;
#ifdef _OPENMP
            printf("Time (%i tread(s)): %g ms\n", threads_num, (double)(t.count() * 1000.0));
#endif
#endif
            fprintf(out, "P6\n%lu\n%lu\n%lu\n", width, height, max_value);
            fwrite(data, sizeof(unsigned char), count, out);
            free(data);
            fclose(out);
        } else {
            printf("Unknown file format: %c%c", x, y);
            fclose(in);
            return -1;
        }
    } else {
        printf("Unknown file format");
        fclose(in);
        return -1;
    }
    return 0;
}
