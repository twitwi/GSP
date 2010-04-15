#ifndef __RAW_TO_IPL_HPP
#define __RAW_TO_IPL_HPP

#include <framework.h>
#include <opencv/cxcore.h>

class RawToIpl
{
private:
  IplImage *img;
public:
  RawToIpl();
  ~RawToIpl();

  void input(void *data, int w, int h, int widthStep, int type);
  Framework _framework;
};

CLASS_AS_MODULE(RawToIpl);

#endif // __RAW_TO_IPL_HPP
