#!/bin/sh

SOURCE_FILE="Main.java"
CLASS_NAME="Main"

cd /code

# Compile Java file
javac "$SOURCE_FILE" 2> compile_error.txt
if [ $? -ne 0 ]; then
  cat compile_error.txt
  exit 1
fi

# Run Java program
timeout 5 java "$CLASS_NAME" < input.txt
