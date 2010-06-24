#include "HistoDetector.hpp"

#include <iostream>

using namespace std;

HistoDetector::HistoDetector()
  : histo(1,1)
{
}

HistoDetector::~HistoDetector()
{
}

IplImage* HistoDetector::computeDetectionImage( ROIExtend & roi)
{
  int y1 = roi.getTop();
  int y2 = roi.getBottom();
  int x1 = roi.getLeft();
  int x2 = roi.getRight();
  int w = (roi.getRight()-roi.getLeft());
  int h = (roi.getBottom()-roi.getTop());
  
  IplImage *detectionImage = cvCreateImage(cvSize(w,h),IPL_DEPTH_8U,1);
  
  cvSetImageROI( currentImage_, cvRect(x1, y1, w, h));
  
  histo.calcBackProject( currentImage_, detectionImage );
  
  cvResetImageROI( currentImage_ );
  
  return detectionImage;
}

void HistoDetector::setFilename( char* filename )
{
  histo.load(filename);
}

void HistoDetector::input( IplImage* img )
{
  mut.lock();
  setCurrentImage(img);
  mut.unlock();
}

void HistoDetector::inputROI(void* rois)
{
  mut.lock();
  std::list<ROIExtend> * p_rois = static_cast<std::list<ROIExtend>*>(rois);
  computeDetectionImages( *p_rois );
  mut.unlock();
  
  if( !p_rois->empty())
    emitNamedEvent("output", getDetectionImages().front());
}

void HistoDetector::inputSelection( int x0, int y0, int x1, int y1, IplImage* img)
{
  mut.lock();
  setCurrentImage(img);
  
  ROIExtend roi(x0, y0, x1, y0, x1, y1, x0, y1);
  
  IplImage *tmp = computeDetectionImage( roi );
  
  mut.unlock();
  
  emitNamedEvent("output", tmp);
  cvReleaseImage(&tmp);
}

void HistoDetector::inputPoints( std::vector<CvPoint>* points)
{
  mut.lock();
  
  mut.unlock();
}
