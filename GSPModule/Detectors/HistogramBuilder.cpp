#include "HistogramBuilder.hpp"

HistogramBuilder::HistogramBuilder()
  :size1(10)
  ,size2(10)
{
}

HistogramBuilder::~HistogramBuilder()
{
}

void HistogramBuilder::initModule()
{
}

void HistogramBuilder::stopModule()
{
}

void HistogramBuilder::input(int x0, int y0, int x1, int y1, IplImage* img)
{
  Histogram2D histo(size1, size2);

  CvMat mat;
  cvGetSubRect(img, &mat, cvRect(x0, y0, x1-x0, y1-y0));
  
  histo.calcHist(&mat);
  histo.save(filename.c_str());
}

void HistogramBuilder::setFilename( char *name )
{
  filename=name;
}

void HistogramBuilder::setSize1(int s)
{
  size1 = s;
}

void HistogramBuilder::setSize2(int s)
{
  size2 = s;
}
