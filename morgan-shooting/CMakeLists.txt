cmake_minimum_required(VERSION 3.16)
PROJECT(shooting)

set(CMAKE_BUILD_TYPE Debug CACHE STRING "set build type to debug")
# SET(CMAKE_CXX_FLAGS_DEBUG "$ENV{CXXFLAGS} -O0 -Wall -g -ggdb")
set(CMAKE_CXX_FLAGS ${CMAKE_CXX_FLAGS} -g)

INCLUDE(${CMAKE_CURRENT_SOURCE_DIR}/include.cmake)

AUX_SOURCE_DIRECTORY(./jni SRC_JNI)
AUX_SOURCE_DIRECTORY(./utils SRC_UTILS)
AUX_SOURCE_DIRECTORY(./game/. SRC_GAME)
AUX_SOURCE_DIRECTORY(./game/physics SRC_PHYSICS)
AUX_SOURCE_DIRECTORY(./game/tasks SRC_TASKS)
AUX_SOURCE_DIRECTORY(./msg SRC_MSG)

ADD_LIBRARY(nativeshooting SHARED 
        ${SRC_JNI} 
        ${SRC_UTILS} 
        ${SRC_GAME}
        ${SRC_PHYSICS}
        ${SRC_TASKS}
        ${SRC_MSG}
        )

