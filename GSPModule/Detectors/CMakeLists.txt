if(OMISCID_FOUND AND LIBXML2_FOUND)
  add_library( Detector SHARED 
      HistoColorDetector.cpp 
      HistoColorDetector.hpp 
      HistoDetector.cpp 
      HistoDetector.hpp 
      Detector.cpp 
      Detector.hpp 
      ROIExtend.cpp 
      ROIExtend.hpp 
      Histo.cpp 
      Histo.hpp )
  target_link_libraries( Detector ${Boost_THREAD_LIBRARY} ${OpenCV_LIBS} )

  add_library( Histo SHARED 
      HistogramBuilder.cpp
      HistogramBuilder.hpp
      Histo.cpp
      Histo.hpp)
  target_link_libraries( Histo ${Boost_THREAD_LIBRARY} ${OpenCV_LIBS} )

  add_library( DetectorService SHARED 
      DetectorService.cpp
      DetectorService.hpp )
  target_link_libraries( DetectorService Detector ${OpenCV_LIBS} )
endif(OMISCID_FOUND AND LIBXML2_FOUND)

