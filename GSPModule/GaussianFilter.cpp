#include "GaussianFilter.hpp"
#include <cstdio>

GaussianFilter::GaussianFilter()
  : img(0)
  , kernel_width(5)
  , kernel_height(5)
  , sigma(1.)
{
}

void GaussianFilter::initModule()
{
}

void GaussianFilter::stopModule()
{
  if(img)
    cvReleaseImage(&img);
}

void GaussianFilter::input(IplImage *inputImg)
{
  if(!img)
    img = cvCloneImage(inputImg);
  else if(img->width != inputImg->width
          || img->height != inputImg->height
          || img->depth != inputImg->depth
          || img->nChannels != inputImg->nChannels)
  {
    cvReleaseImage(&img);
    img = cvCloneImage(inputImg);
  }
  
  cvSmooth(inputImg, img, CV_GAUSSIAN, kernel_width, kernel_height, sigma);
  emitNamedEvent("output", img);
}

void GaussianFilter::setSigma(float val)
{
  sigma = val;
}

void GaussianFilter::setKernelWidth(int w)
{
  kernel_width = w;
}
void GaussianFilter::setKernelHeight(int h)
{
  kernel_height = h;
}
