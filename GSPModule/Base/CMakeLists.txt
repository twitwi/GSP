include_directories(${CMAKE_CURRENT_SOURCE_DIR})

add_library( ImageLoader SHARED ImageLoader.cpp ImageLoader.hpp )
target_link_libraries( ImageLoader ${Boost_THREAD_LIBRARY} ${OpenCV_LIBS} )

add_library( ImageSource SHARED ImageSource.cpp ImageSource.hpp )
target_link_libraries( ImageSource ${Boost_THREAD_LIBRARY} ${OpenCV_LIBS} )

add_library( ImageViewer SHARED ImageViewer.cpp ImageViewer.hpp )
target_link_libraries( ImageViewer 
    ${OpenCV_LIBS} ${GTK2_LIBRARIES} ${GTHREAD_LIBRARIES} )

add_library( RawToIpl SHARED RawToIpl.cpp RawToIpl.hpp )
target_link_libraries( RawToIpl ${OpenCV_LIBS} )

add_library( GTKModule SHARED GTKModule.cpp GTKModule.hpp )
target_link_libraries( GTKModule 
    ${Boost_THREAD_LIBRARY} ${GTHREAD_LIBRARIES} ${GTK2_LIBRARIES} )

add_library( ColorModules SHARED 
    ColorConvertor.cpp 
    ColorConvertor.hpp 
    ColorFilter.cpp
    ColorFilter.hpp
    ColorAnalyser.cpp 
    ColorAnalyser.hpp 
    ColorSeqDetector.cpp
    ColorSeqDetector.hpp )
target_link_libraries( ColorModules ${OpenCV_LIBS} )

add_library( ImageModules SHARED GaussianFilter.cpp GaussianFilter.hpp )
target_link_libraries( ImageModules ${OpenCV_LIBS} )

if( SERVICE_VIDEO_LIB AND OMISCID_FOUND AND LIBXML2_FOUND )
  add_library( ServiceVideoModule SHARED 
      ServiceVideoModule.cpp ServiceVideoModule.hpp )
  target_link_libraries( ServiceVideoModule 
      ${SERVICE_VIDEO_LIB} ${OMISCID_LIBRARIES} )

  add_library( OmiscidVideoGrabber SHARED 
      OmiscidVideoGrabber.cpp OmiscidVideoGrabber.hpp )
  target_link_libraries( OmiscidVideoGrabber 
      ${Boost_THREAD_LIBRARY} 
      ${OpenCV_LIBS} 
      ${SERVICE_VIDEO_LIB} 
      ${OMISCID_LIBRARIES} )
endif( SERVICE_VIDEO_LIB AND OMISCID_FOUND AND LIBXML2_FOUND )

if( OPENGL_FOUND AND GLEW_FOUND )
  add_library( GLModule SHARED GLModule.cpp GLModule.hpp )
  target_link_libraries( GLModule  ${OpenGL_LIBRARIES} ${GLEW_LIBRARY})
  
  add_library( TestGLModule SHARED TestGLModule.cpp TestGLModule.hpp )
  target_link_libraries( TestGLModule GLModule ${Boost_THREAD_LIBRARY})
  
  add_library( LadybugCubeMap SHARED 
      CubeMapModule.cpp 
      CubeMapModule.hpp 
      LadybugCubeMap.cpp 
      LadybugCubeMap.hpp )
  target_link_libraries( LadybugCubeMap 
      GLModule ${Boost_THREAD_LIBRARY} ${OpenCV_LIBS} )

  add_library( CatadioptricCubeMap SHARED 
      CubeMapModule.cpp
      CubeMapModule.hpp
      CatadioptricCubeMap.cpp
      CatadioptricCubeMap.hpp )
  target_link_libraries( CatadioptricCubeMap 
      GLModule ${Boost_THREAD_LIBRARY} ${OpenCV_LIBS} ${LIBXML2_LIBRARIES} )
  
  add_library( SphericMap SHARED SphericMap.cpp SphericMap.hpp )
  target_link_libraries( SphericMap 
      GLModule ${Boost_THREAD_LIBRARY} ${OpenCV_LIBS} )
endif( OPENGL_FOUND AND GLEW_FOUND )
