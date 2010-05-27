#include "Histo.hpp"

#include <fstream>

using namespace std;

Histogram2D::Histogram2D( int size1, int size2 )
{
  int h_bins = size1, s_bins = size2;
  int hist_size[] = {h_bins, s_bins};
  /* hue varies from 0 (~0 deg red) to 180 (~360 deg red again) */
  float h_ranges[] = { 0, 180 };
  /* saturation varies from 0 (black-gray-white) to
     255 (pure spectrum color) */
  float s_ranges[] = { 0, 255 };
  float* ranges[] = { h_ranges, s_ranges };
  
  hist = cvCreateHist( 2, hist_size, CV_HIST_ARRAY, ranges, 1 );
}

Histogram2D::Histogram2D( const char* filename )
  : hist(0)
{
  load( filename );
}


Histogram2D::~Histogram2D()
{
  if(hist)
  {
    cvReleaseHist(&hist);
  }
}

void Histogram2D::load( const char* filename )
{
  if(hist)
  {
    cvReleaseHist(&hist);
  }
  
  ifstream f(filename);

  int h_bins, s_bins;
  
  f >> h_bins;
  f >> s_bins;
  
  int hist_size[] = {h_bins, s_bins};
  /* hue varies from 0 (~0 deg red) to 180 (~360 deg red again) */
  float h_ranges[] = { 0, 180 };
  /* saturation varies from 0 (black-gray-white) to
     255 (pure spectrum color) */
  float s_ranges[] = { 0, 255 };
  float* ranges[] = { h_ranges, s_ranges };
  
  hist = cvCreateHist( 2, hist_size, CV_HIST_ARRAY, ranges, 1 );
  

  for(int y=0; y<s_bins; y++)
    for(int x=0; x<h_bins; x++)
    {
      double val;
      f >> val;
      cvSet2D(hist->bins, x, y, cvRealScalar(val));
    }
}

void Histogram2D::save( const char* filename )
{
  if(hist)
  {
    ofstream f(filename);
    int h_bins = cvGetDimSize(hist->bins, 0);
    int s_bins = cvGetDimSize(hist->bins, 1);
    
    f << h_bins << endl;
    f << s_bins << endl;

    
    for(int y=0; y<s_bins; y++)
    {
      
      for(int x=0; x<h_bins; x++)
      {
        f << *cvGetHistValue_2D(hist, x, y) << " ";
      }
      f << endl;
    }
  }
}

void Histogram2D::calcHist( CvArr * src )
{
  IplImage* h_plane = cvCreateImage( cvGetSize(src), 8, 1 );
  IplImage* s_plane = cvCreateImage( cvGetSize(src), 8, 1 );
  IplImage* v_plane = cvCreateImage( cvGetSize(src), 8, 1 );
  IplImage* planes[] = { h_plane, s_plane };  
  IplImage* hsv = cvCreateImage( cvGetSize(src), 8, 3 );
  
  cvCvtColor( src, hsv, CV_BGR2HSV );
  cvCvtPixToPlane( hsv, h_plane, s_plane, v_plane, 0 );
  
  cvCalcHist( planes, hist, 0, 0 );

  cvReleaseImage( &h_plane );
  cvReleaseImage( &s_plane );
  cvReleaseImage( &v_plane );
  cvReleaseImage( &hsv );
}

void Histogram2D::calcBackProject( IplImage *src, IplImage *dst )
{
  IplImage* h_plane = cvCreateImage( cvGetSize(src), 8, 1 );
  IplImage* s_plane = cvCreateImage( cvGetSize(src), 8, 1 );
  IplImage* v_plane = cvCreateImage( cvGetSize(src), 8, 1 );
  IplImage* planes[] = { h_plane, s_plane };
  IplImage* hsv = cvCreateImage( cvGetSize(src), 8, 3 );
  cvCvtColor( src, hsv, CV_BGR2HSV );
  cvCvtPixToPlane( hsv, h_plane, s_plane, v_plane, 0 );
  
  cvCalcBackProject( planes, dst, hist );
  
  cvReleaseImage( &h_plane );
  cvReleaseImage( &s_plane );
  cvReleaseImage( &v_plane );
  cvReleaseImage( &hsv );
}
