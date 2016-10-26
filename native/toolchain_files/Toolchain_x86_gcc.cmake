# AUTHOR: Felix Braam
# EMAIL: felix.braam@ise.fraunhofer.de
# INFO: cmake toolchain file to cross compile for linux x86 on an amd64 platform.
#       needs g++-multilib installed:
#       sudo apt-get install g++-mulitlib
# 
# the name of the target operating system
set(CMAKE_SYSTEM_NAME Linux)
set(CMAKE_SYSTEM_PROCESSOR x86)
# Which compilers to use for C and C++
set(CMAKE_C_COMPILER gcc -m32)
set(CMAKE_CXX_COMPILER g++ -m32)
