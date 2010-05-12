#ifndef __HISTO_HPP_
#define __HISTO_HPP_

#include <opencv/cxcore.h>
#include <opencv/cv.h>


class Histogram2D
{
protected:
  CvHistogram* hist;

public:
  Histogram2D( int size1, int size2 );
  Histogram2D( const char* filename );
  ~Histogram2D();
  
  void load( const char* filename );
  void save( const char* filename );

  void calcHist( CvArr * src );
  void calcBackProject( IplImage *src, IplImage *dst );
};

#endif //__HISTO_HPP_
