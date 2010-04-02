add_library( Detectors SHARED HistoColorDetector Detector ROIExtend)
target_link_libraries( Detectors ${Boost_THREAD_LIBRARY} ${OpenCV_CXCORE_LIBRARY} ${OpenCV_HIGHGUI_LIBRARY})

add_library( DetectorService SHARED DetectorService)
target_link_libraries( DetectorService Detectors ${OpenCV_CXCORE_LIBRARY} ${Omiscid_LIBRARIES})

