#include <framework.h>
#include <opencv/cxcore.h>
#include <opencv/cv.h>
#include <string>

/**
 * Color Filter Module
 * Inputs :
 *   - input (IplImage*)
 * Output :
 *   - output (IplImage*)
 * Parameter :
 *   - Filter (string) (ex:"128-156;53-56;64-64")
 */

class ColorFilter
{
private:
  IplImage *img;
  double rmin, rmax;
  double gmin, gmax;
  double bmin, bmax;
  
public:
  ColorFilter();
  void initModule();
  void stopModule();
  void input(IplImage *inputImg);
  void setFilter(char *filter);

  Framework _framework;
};

CLASS_AS_MODULE(ColorFilter);
