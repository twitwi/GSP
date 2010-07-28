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
  int w = x2-x1;
  int h = y2-y1;
  
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

void HistoDetector::inputROI(std::list<ROIExtend>* rois, int peerId)
{
  mut.lock();
  computeDetectionImages( *rois );
  mut.unlock();
  
  emitNamedEvent("outputROI", detectionImages_, peerId);
  
  if( !rois->empty())
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

void HistoDetector::inputPoints( std::vector<CvPoint>* points, int peerId)
{
  mut.lock();
  std::vector<float> values(points->size());

  for(int i=0; i<points->size(); i++)
  {
    unsigned char * ptr = cvPtr2D( currentImage_, (*points)[i].y, (*points)[i].x);
    values[i] = histo.getValue(ptr[0], ptr[1], ptr[2]);
  }

  std::vector<float> *p_values;
  emitNamedEvent("outputPoints", p_values, peerId);
  
  mut.unlock();
}

void HistoDetector::inputClick( int x, int y, IplImage* img)
{
  unsigned char * ptr = (unsigned char*) &(img->imageData)[img->widthStep*y+3*x];

  float val = histo.getValue(ptr[2], ptr[1], ptr[0]);

  cout << val << endl;
}
