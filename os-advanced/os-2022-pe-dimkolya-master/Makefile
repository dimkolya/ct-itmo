all:
    g++ pe_parser.cpp -o pe-parser

clean:
    rm *.o

validation-pe-tests: all
    python3 -m tests ValidatingPeTestCases -f

import-dll-tests: all
    python3 -m tests ImportDllTestCases -f

import-function-tests: all
    python3 -m tests ImportFunctionTestCases -f