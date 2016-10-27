#!/bin/bash

# see http://www.vtk.org/Wiki/Eclipse_CDT4_Generator for detailed description

cd $(dirname $0)

build_dir=build/native

mkdir -p ${build_dir}

cd ${build_dir}

cmake -G"Eclipse CDT4 - Unix Makefiles"  \
      -DCMAKE_ECLIPSE_GENERATE_SOURCE_PROJECT=TRUE -DCMAKE_ECLIPSE_VERSION=4.6 \
      -DCMAKE_BUILD_TYPE=Debug ../../native/


echo "Open Eclipse (CDT) and import the native" \
     "part from <project_root>/${build_dir}/"
