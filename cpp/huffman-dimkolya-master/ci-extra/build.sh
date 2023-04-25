#!/bin/bash
set -euo pipefail
IFS=$' \t\n'

mkdir -p cmake-build-$1
rm -rf cmake-build-$1/*
#mkdir -p cmake-build-$1/data
#ln ../unit-tests/data/rus.txt cmake-build-$1/data/rus.txt
#ln ../unit-tests/data/eng.txt cmake-build-$1/data/eng.txt
#ln ../unit-tests/data/out.ppm cmake-build-$1/data/out.ppm
cmake "-DCMAKE_TOOLCHAIN_FILE=../vcpkg/scripts/buildsystems/vcpkg.cmake" -GNinja --preset $1 -DENABLE_SLOW_TEST=ON -S .
cmake --build cmake-build-$1
