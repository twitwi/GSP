#ifndef __HISTO_COLOR_DETECTOR_HPP_
#define __HISTO_COLOR_DETECTOR_HPP_

#include "Histo.hpp"
#include "Detector.hpp"
#include <framework.h>
#include <boost/thread/mutex.hpp>

class HistoDetector : public Detector
{
private:
  Histogram2D histo;
  boost::mutex mut;
  
public:
  HistoDetector();
  ~HistoDetector();
  
  IplImage* computeDetectionImage( ROIExtend & roi);
  
  void setFilename( char* filename );
  
  /**
   * signal to compute new image
   */
  void input( IplImage* img );
  
  void inputROI(void* rois);
  
  void inputSelection( int x0, int y0, int x1, int y1, IplImage* img);
  
  Framework _framework;
};

CLASS_AS_MODULE(HistoDetector);

#endif // __HISTO_COLOR_DETECTOR_HPP_
