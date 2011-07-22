#include <framework.h>
#include <opencv/cxcore.h>
#include <opencv/cv.h>
#include <string>

/**
 * Image Convertor Module
 * Inputs :
 *   - input (IplImage*)
 * Output :
 *   - output (IplImage*)
 * Parameter :
 *   - format (string)
 */

class ColorConvertor
{
private:
  IplImage *img;
  std::string format;
  
public:
  ColorConvertor();
  void initModule();
  void stopModule();
  void input(IplImage *inputImg);
  void setFormat(char *format);

  Framework _framework;
};

CLASS_AS_MODULE(ColorConvertor);
