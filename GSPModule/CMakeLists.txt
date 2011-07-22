project ( GSPModule )

cmake_minimum_required(VERSION 2.6)

set(CMAKE_MODULE_PATH ${PROJECT_SOURCE_DIR}/CMake)
set(Boost_USE_MULTITHREADED ON)

find_package( Boost COMPONENTS thread date_time)
find_package( GTK2 COMPONENTS gtk )
find_package( GObject REQUIRED ) # for gthreads
find_package( OpenCV REQUIRED )

find_package( OpenGL )
find_package( GLEW )
find_package( LibXml2 )
find_package( Omiscid )

find_library( SERVICE_VIDEO_LIB servicevideo )

include_directories(
  ${BOOST_INCLUDEDIR}
  ${GLEW_INCLUDE_DIR}
  ${GOBJECT_INCLUDE_DIR}
  ${GTK2_INCLUDE_DIRS}
  ${OMISCID_INCLUDE_DIR}
  ${OpenCV_INCLUDE_DIRS}
  ${OPENGL_INCLUDE_DIR}
  ${LIBXML2_INCLUDE_DIR}
  /usr/include/servicevideo # Yuuuuurk
  ${CMAKE_CURRENT_SOURCE_DIR}
  )

link_directories(
  ${BOOST_LIBRARYDIR}
  ${GLEW_LIBRARY_DIR}
  ${GTHREAD_LIBRARY_DIRS}
  ${Omiscid_LIBRARY_DIRS}
  ${OpenCV_LIB_DIR}
  )

add_definitions( ${GOBJECT_DEFINITIONS} )

set( LIBRARY_OUTPUT_PATH ${CMAKE_CURRENT_BINARY_DIR} )

add_subdirectory( Base )
add_subdirectory( Detectors )
