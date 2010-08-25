#include <framework.h>
#include <opencv/cxcore.h>
#include <opencv/cv.h>
#include <string>

/**
 * Gaussian Filter Module
 * Inputs :
 *   - input (IplImage*)
 * Output :
 *   - output (IplImage*)
 * Parameter :
 *   - sigma (double)
 */

class GaussianFilter
{
private:
  IplImage *img;
  double sigma;
  int kernel_width;
  int kernel_height;
  
public:
  GaussianFilter();
  void initModule();
  void stopModule();
  void input(IplImage *inputImg);
  void setSigma(float);
  void setKernelWidth(int);
  void setKernelHeight(int);

  Framework _framework;
};

CLASS_AS_MODULE(GaussianFilter);
