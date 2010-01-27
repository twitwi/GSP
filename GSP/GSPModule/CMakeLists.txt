project ( GSPModule )

cmake_minimum_required(VERSION 2.6)

set(Boost_USE_MULTITHREADED ON)
find_package( Boost COMPONENTS thread)

set(CMAKE_MODULE_PATH ${PROJECT_SOURCE_DIR}/CMake)

FIND_PACKAGE( OpenCV REQUIRED )
INCLUDE_DIRECTORIES( ${OPENCV_INCLUDE_DIR} ${PROJECT_SOURCE_DIR})

add_library( ImageLoader SHARED ImageLoader )
target_link_libraries( ImageLoader ${Boost_LIBRARIES} ${OPENCV_LIBRARIES})

add_library( ImageViewer SHARED ImageViewer )
target_link_libraries( ImageViewer ${OPENCV_LIBRARIES})