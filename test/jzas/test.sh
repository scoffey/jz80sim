#!/bin/bash

# Testeos sintacticos
cd sintactic
./test.sh
cd ..

# Testeos de la tabla de símbolos
cd symboltable
./test.sh
cd ..

# Testeos semánticos
cd semantic
./test.sh
cd ..

# Testeos de integracion
# TODO
