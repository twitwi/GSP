#include "ColorFilter.hpp"
#include <cstdio>

ColorFilter::ColorFilter()
  : img(0)
{
}

void ColorFilter::initModule()
{
}

void ColorFilter::stopModule()
{
  if(img)
    cvReleaseImage(&img);
}

void ColorFilter::input(IplImage *inputImg)
{
  if(!img)
    img = cvCreateImage(cvGetSize(inputImg), IPL_DEPTH_8U, 1);
  else if(img->width != inputImg->width
          || img->height != inputImg->height)
  {
    cvReleaseImage(&img);
    img = cvCreateImage(cvGetSize(inputImg), IPL_DEPTH_8U, 1);
  }
  
  cvInRangeS(inputImg,
           cvScalar(rmin, gmin, bmin),
           cvScalar(rmax, gmax, bmax),
           img);
  emitNamedEvent("output", img);
}

void ColorFilter::setFilter(char *filter)
{
  sscanf(filter, "%lf-%lf;%lf-%lf;%lf-%lf",
         &rmin, &rmax,
         &gmin, &gmax,
         &bmin, &bmax);
}
