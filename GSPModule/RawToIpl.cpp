#include "RawToIpl.hpp"

#include <iostream>

RawToIpl::RawToIpl()
{
  img = cvCreateImageHeader(cvSize(100,100), IPL_DEPTH_8U, 3);
}

RawToIpl::~RawToIpl()
{
  cvReleaseImageHeader(&img);
}

void RawToIpl::input( void *data, int w, int h, int widthStep, int type )
{
  
  
  img->nChannels = type/8;
  img->width = w;
  img->height = h;
  img->widthStep = widthStep;
  img->imageSize = h * widthStep;
  img->imageData = (char*) data;
  
  emitNamedEvent("output", img );
}
