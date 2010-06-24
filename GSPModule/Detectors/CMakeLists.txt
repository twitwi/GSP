add_library( Detector SHARED HistoColorDetector HistoDetector Detector ROIExtend Histo )
target_link_libraries( Detector ${Boost_THREAD_LIBRARY} ${OpenCV_CXCORE_LIBRARY} ${OpenCV_HIGHGUI_LIBRARY})

add_library( Histo SHARED HistogramBuilder Histo)
target_link_libraries( Histo ${Boost_THREAD_LIBRARY} ${OpenCV_CXCORE_LIBRARY} ${OpenCV_HIGHGUI_LIBRARY})


add_library( DetectorService SHARED DetectorService)
target_link_libraries( DetectorService Detector ${OpenCV_CXCORE_LIBRARY} ${Omiscid_LIBRARIES})

