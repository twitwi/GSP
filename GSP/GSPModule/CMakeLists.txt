project ( GSPModule )

cmake_minimum_required(VERSION 2.6)

find_package( PkgConfig )

PKG_CHECK_MODULES(GTK REQUIRED gtk+-2.0)
PKG_CHECK_MODULES(GTHREAD REQUIRED gthread-2.0)

set(CMAKE_MODULE_PATH ${PROJECT_SOURCE_DIR}/CMake)

FIND_PACKAGE( OpenCV COMPONENTS CV CXCORE HIGHGUI )

find_package(Omiscid REQUIRED)

set(Boost_USE_MULTITHREADED ON)
find_package( Boost COMPONENTS thread date_time)

find_package( OpenGL )

find_library( SERVICE_VIDEO_LIB servicevideo )
find_library( GLEW_LIB GLEW )

INCLUDE_DIRECTORIES(${BOOST_INCLUDEDIR}
  ${GTK_INCLUDE_DIRS}
  ${GTHREAD_INCLUDE_DIRS}
  ${OpenCV_INCLUDE_DIR}
  ${Omiscid_INCLUDE_DIR}
  /usr/include/servicevideo
  ${PROJECT_SOURCE_DIR}
  ${OPENGL_INCLUDE_DIR}
  )

LINK_DIRECTORIES(
  ${BOOST_LIBRARYDIR}
  ${GTK_LIBRARY_DIRS}
  ${GTHREAD_LIBRARY_DIRS}
  ${OPENCV_LIBRARY_DIR}
  ${Omiscid_LIBRARY_DIRS}
  )

add_library( ImageLoader SHARED ImageLoader )
target_link_libraries( ImageLoader ${Boost_THREAD_LIBRARY} ${OpenCV_CXCORE_LIBRARY} ${OpenCV_HIGHGUI_LIBRARY})

add_library( ImageViewer SHARED ImageViewer )
target_link_libraries( ImageViewer ${OpenCV_CXCORE_LIBRARY} ${OpenCV_HIGHGUI_LIBRARY} ${GTK_LIBRARIES} ${GTHREAD_LIBRARIES})

add_library( GTKModule SHARED GTKModule )
target_link_libraries( GTKModule ${Boost_THREAD_LIBRARY} ${GTHREAD_LIBRARIES} ${GTK_LIBRARIES} )

set( CMAKE_CXX_FLAGS "${Omiscid_CFLAGS}")

if( SERVICE_VIDEO_LIB AND Omiscid_FOUND )
  add_library( ServiceVideoModule SHARED ServiceVideoModule )
  target_link_libraries( ServiceVideoModule ${SERVICE_VIDEO_LIB} ${Omiscid_LIBRARIES} )
endif( SERVICE_VIDEO_LIB AND Omiscid_FOUND )


if( OPENGL_FOUND AND GLEW_LIB )
  add_library( GLModule SHARED GLModule )
  target_link_libraries( GLModule  ${OPENGL_LIBRARIES} ${GLEW_LIB})
  
  add_library( TestGLModule SHARED TestGLModule )
  target_link_libraries( TestGLModule GLModule ${Boost_THREAD_LIBRARY})
endif( OPENGL_FOUND AND GLEW_LIB )
