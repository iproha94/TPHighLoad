cmake_minimum_required(VERSION 3.4)
project(explorer_cache)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

set(SOURCE_FILES
    create_list.cpp
    create_list.h
    main.cpp
    main_test.cpp)

add_executable(explorer_cache ${SOURCE_FILES})

