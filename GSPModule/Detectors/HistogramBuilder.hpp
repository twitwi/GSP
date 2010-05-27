#ifndef __HISTOGRAM_BUILDER_HPP
#define __HISTOGRAM_BUILDER_HPP

#include "Histo.hpp"
#include <framework.h>

#include <string>

class HistogramBuilder
{
private:
  std::string filename;
  int size1, size2;
  
public:
  HistogramBuilder();
  ~HistogramBuilder();
  
  void initModule();
  void stopModule();
  void input(int x0, int y0, int x1, int y1, IplImage* img);
  void setFilename(char *name);
  void setSize1(int s);
  void setSize2(int s);
  Framework _framework;
};

CLASS_AS_MODULE(HistogramBuilder);

#endif
