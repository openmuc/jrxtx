cmake_minimum_required(VERSION 2.8)
project(jRXTX)

set(PROJECT_SRC_DIR "${CMAKE_SOURCE_DIR}/src")

file(GLOB_RECURSE PROJECT_SOURCE_FILES  "${PROJECT_SRC_DIR}/*.c")

#list(APPEND EXECUTABLE_NAMES "${PROJECT_NAME}Test")
#list(APPEND EXECUTABLE_FILES "${PROJECT_SRC_DIR}/test.cpp")

include_directories(${PROJECT_SOURCE_DIR})

#add_definitions("-std=c++11")

message(STATUS "${PROJECT_SOURCE_FILES}")
#compile everything in a library to use with every program
add_library(${PROJECT_NAME} SHARED ${PROJECT_SOURCE_FILES})

install(TARGETS ${PROJECT_NAME} LIBRARY DESTINATION lib)
