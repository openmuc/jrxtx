#!/bin/sh
set -euo pipefail

./autogen.sh

./configure

make clean
make
