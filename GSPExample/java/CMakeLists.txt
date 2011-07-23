project ( GSPExample-java Java )

cmake_minimum_required( VERSION 2.6 )
find_package(Java REQUIRED)
include(UseJava.cmake)

set(CMAKE_JAVA_INCLUDE_PATH
    ${CMAKE_CURRENT_SOURCE_DIR}/../../GSPFramework/target/dependency/*:${CMAKE_CURRENT_SOURCE_DIR}/../../GSPFramework/target/gsp-framework-1.1-SNAPSHOT.jar)

file(GLOB_RECURSE GSPExampleJava_SRCS fr/*.java)
add_jar(GSPExampleJava ${GSPExampleJava_SRCS})

