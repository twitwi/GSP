#include "ColorConvertor.hpp"

ColorConvertor::ColorConvertor()
  : img(0)
{
}

void ColorConvertor::initModule()
{
}

void ColorConvertor::stopModule()
{
  if(img)
    cvReleaseImage(&img);
}

void ColorConvertor::input(IplImage *inputImg)
{
  if(!img)
    img = cvCloneImage(inputImg);
  else if(img->width != inputImg->width
          || img->height != inputImg->height
          || img->nChannels != inputImg->nChannels
          || img->depth != inputImg->depth)
  {
    cvReleaseImage(&img);
    img = cvCloneImage(inputImg);
  }
  
  if(format == "RGB2HSV")
  {
    cvCvtColor(inputImg, img, CV_RGB2HSV);
    emitNamedEvent("output", img);
  }
  if(format == "RGB2BGR")
  {
    cvCvtColor(inputImg, img, CV_RGB2BGR);
    emitNamedEvent("output", img);
  }
  if(format == "BGR2HSV")
  {
    cvCvtColor(inputImg, img, CV_BGR2HSV);
    emitNamedEvent("output", img);
  }
  if(format == "BGR2RGB")
  {
    cvCvtColor(inputImg, img, CV_BGR2RGB);
    emitNamedEvent("output", img);
  }
  
}

void ColorConvertor::setFormat(char *format)
{
  this->format = format;
}
