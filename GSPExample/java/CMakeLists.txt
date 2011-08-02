project ( GSPExample-java Java )

cmake_minimum_required( VERSION 2.6 )
if( ${CMAKE_VERSION} VERSION_LESS 2.8.4 )
  include( FindJava.cmake )
else( ${CMAKE_VERSION} VERSION_LESS 2.8.4 )
  find_package( Java REQUIRED )
endif( ${CMAKE_VERSION} VERSION_LESS 2.8.4 )
include( UseJava.cmake )

set( CMAKE_JAVA_INCLUDE_PATH
    ${CMAKE_CURRENT_SOURCE_DIR}/../../GSPFramework/target/dependency/*:${CMAKE_CURRENT_SOURCE_DIR}/../../GSPFramework/target/gsp-framework-1.1-SNAPSHOT.jar )

file( GLOB_RECURSE GSPExampleJava_SRCS fr/*.java )
add_jar( GSPExampleJava ${GSPExampleJava_SRCS} )

